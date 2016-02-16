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
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfigurer;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
@Configuration
public class HibernatePackageBuilder extends AbstractFactoryBean<HibernatePackage>
{
	@Autowired
	private AcrossContextInfo context;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AbstractHibernatePackageModule currentModule;

	@Override
	public Class<?> getObjectType() {
		return HibernatePackage.class;
	}

	@Override
	protected HibernatePackage createInstance() throws Exception {
		HibernatePackage hibernatePackage = new HibernatePackage( currentModule.getName() );

		currentModule.getHibernatePackageProviders().forEach( hibernatePackage::add );

		if ( currentModule.isScanForHibernatePackages() ) {
			Set<HibernatePackageConfigurer> configurers = new HashSet<>();

			context.getModules().stream()
			       .filter( acrossModuleInfo -> acrossModuleInfo.getModule() instanceof HibernatePackageConfigurer )
			       .forEach( acrossModuleInfo -> configurers.add(
					       (HibernatePackageConfigurer) acrossModuleInfo.getModule() ) );

			( (ListableBeanFactory) getBeanFactory() )
					.getBeansOfType( HibernatePackageConfigurer.class )
					.forEach( ( name, configurer ) -> configurers.add( configurer ) );

			configurers.forEach( c -> c.configureHibernatePackage( hibernatePackage ) );
		}

		return hibernatePackage;
	}
}
