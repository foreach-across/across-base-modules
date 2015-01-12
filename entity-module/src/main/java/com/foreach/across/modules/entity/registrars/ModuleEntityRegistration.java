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
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.events.AcrossModuleBootstrappedEvent;
import com.foreach.across.modules.entity.business.MutableEntityRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Takes care of running all registered {@link com.foreach.across.modules.entity.registrars.EntityRegistrar}
 * instances when a module has bootstrapped, and afterwards applying the {@link com.foreach.across.modules.entity.config.EntityConfigurer}
 * instances from the module.
 * <p/>
 * A registrar automatically creates an EntityConfiguration, whereas an
 * {@link com.foreach.across.modules.entity.config.EntityConfigurer} can manually alter it afterwards.
 *
 * @author Arne Vandamme
 */
@AcrossEventHandler
public class ModuleEntityRegistration
{
	@Autowired
	private MutableEntityRegistry entityRegistry;

	@SuppressWarnings("all")
	@RefreshableCollection(includeModuleInternals = true, incremental = true)
	private Collection<EntityRegistrar> registrars;

	@Event
	protected void applyModule( AcrossModuleBootstrappedEvent moduleBootstrappedEvent ) {
		AcrossModuleInfo moduleInfo = moduleBootstrappedEvent.getModule();
		AcrossContextBeanRegistry beanRegistry = AcrossContextUtils.getBeanRegistry( moduleInfo );

		for ( EntityRegistrar registrar : registrars ) {
			registrar.registerEntities( entityRegistry, moduleInfo, beanRegistry );
		}
	}
}
