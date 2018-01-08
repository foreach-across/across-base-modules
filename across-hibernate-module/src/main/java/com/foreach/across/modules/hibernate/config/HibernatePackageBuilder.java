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

package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.DynamicAcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import com.foreach.across.modules.hibernate.jpa.config.JpaModuleProperties;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfigurer;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Responsible for building the resources that should be mapped to the entity manager.
 *
 * @author Arne Vandamme
 */
@Configuration
public class HibernatePackageBuilder extends AbstractFactoryBean<HibernatePackage>
{
	private final AcrossContextInfo contextInfo;
	private final AbstractHibernatePackageModule currentModule;
	private final JpaModuleProperties jpaModuleProperties;

	@Autowired
	public HibernatePackageBuilder( AcrossContextInfo contextInfo,
	                                @Module(AcrossModule.CURRENT_MODULE) AbstractHibernatePackageModule currentModule,
	                                JpaModuleProperties jpaModuleProperties ) {
		this.contextInfo = contextInfo;
		this.currentModule = currentModule;
		this.jpaModuleProperties = jpaModuleProperties;
	}

	@Override
	public Class<?> getObjectType() {
		return HibernatePackage.class;
	}

	@Override
	protected HibernatePackage createInstance() {
		HibernatePackage hibernatePackage = new HibernatePackage( currentModule.getName() );

		currentModule.getHibernatePackageProviders().forEach( hibernatePackage::add );

		if ( currentModule.isScanForHibernatePackages() ) {
			Set<HibernatePackageConfigurer> configurers = new HashSet<>();

			contextInfo.getModules().stream()
			           .filter( acrossModuleInfo -> acrossModuleInfo.getModule() instanceof HibernatePackageConfigurer )
			           .forEach( acrossModuleInfo -> configurers.add(
					           (HibernatePackageConfigurer) acrossModuleInfo.getModule() ) );

			( (ListableBeanFactory) getBeanFactory() )
					.getBeansOfType( HibernatePackageConfigurer.class )
					.forEach( ( name, configurer ) -> configurers.add( configurer ) );

			configurers.forEach( c -> c.configureHibernatePackage( hibernatePackage ) );
		}

		registerDynamicApplicationPackage( hibernatePackage );

		return hibernatePackage;
	}

	private void registerDynamicApplicationPackage( HibernatePackage hibernatePackage ) {
		contextInfo.getModules()
		           .stream()
		           .map( AcrossModuleInfo::getModule )
		           .filter( DynamicAcrossModule.DynamicApplicationModule.class::isInstance )
		           .map( DynamicAcrossModule.DynamicApplicationModule.class::cast )
		           .findFirst()
		           .ifPresent( module -> hibernatePackage.addPackageToScan( module.getBasePackage() ) );

	}
}
