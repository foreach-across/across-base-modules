/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.registrars;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.events.AcrossContextBootstrappedEvent;
import com.foreach.across.core.events.AcrossModuleBootstrappedEvent;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Takes care of running all registered {@link com.foreach.across.modules.entity.registrars.EntityRegistrar}
 * instances when a module has bootstrapped, and afterwards applying the {@link com.foreach.across.modules.entity.config.EntityConfigurer}
 * instances from the module.
 * <p/>
 * A registrar automatically creates an EntityConfiguration, whereas an
 * {@link com.foreach.across.modules.entity.config.EntityConfigurer} provides a builders that can be used to modify
 * existing configurations.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.EntityConfigurer
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 * @see com.foreach.across.modules.entity.registrars.EntityRegistrar
 */
@AcrossEventHandler
public class ModuleEntityRegistration
{
	@Autowired
	private AcrossContextInfo contextInfo;

	@Autowired
	private MutableEntityRegistry entityRegistry;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@SuppressWarnings("all")
	@RefreshableCollection(includeModuleInternals = true, incremental = true)
	private Collection<EntityRegistrar> registrars;

	@PostConstruct
	protected void registerAlreadyBootstrappedModules() {
		for ( AcrossModuleInfo moduleInfo : contextInfo.getModules() ) {
			switch ( moduleInfo.getBootstrapStatus() ) {
				case BootstrapBusy:
				case Bootstrapped:
					applyModule( moduleInfo );
					break;
				default:
					break;
			}
		}
	}

	@Event
	protected void moduleBootstrapped( AcrossModuleBootstrappedEvent moduleBootstrappedEvent ) {
		applyModule( moduleBootstrappedEvent.getModule() );
	}

	@Event
	protected void contextBootstrapped( AcrossContextBootstrappedEvent contextBootstrappedEvent ) {
		AcrossContextBeanRegistry beanRegistry
				= AcrossContextUtils.getBeanRegistry( contextBootstrappedEvent.getContext() );

		List<EntitiesConfigurationBuilder> builders = new ArrayList<>();

		// Configure the builders
		for ( EntityConfigurer configurer : beanRegistry.getBeansOfType( EntityConfigurer.class, true ) ) {
			EntitiesConfigurationBuilder builder = new EntitiesConfigurationBuilder();
			configurer.configure( builder );

			builders.add( builder );
		}

		// Apply the builders to the registry
		for ( EntitiesConfigurationBuilder builder : builders ) {
			builder.apply( entityRegistry, beanFactory );
		}

		// Run the builder post processors
		for ( EntitiesConfigurationBuilder builder : builders ) {
			builder.postProcess( entityRegistry );
		}
	}

	private void applyModule( AcrossModuleInfo moduleInfo ) {
		AcrossContextBeanRegistry beanRegistry = AcrossContextUtils.getBeanRegistry( moduleInfo );

		for ( EntityRegistrar registrar : registrars ) {
			registrar.registerEntities( entityRegistry, moduleInfo, beanRegistry );
		}
	}
}
