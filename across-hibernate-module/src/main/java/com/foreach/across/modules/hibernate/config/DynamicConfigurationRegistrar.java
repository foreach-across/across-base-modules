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

import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.config.dynamic.PersistenceContextInViewConfiguration.OpenSessionFactoryInViewFilterConfiguration;
import com.foreach.across.modules.hibernate.config.dynamic.PersistenceContextInViewConfiguration.OpenSessionFactoryInViewInterceptorConfiguration;
import com.foreach.across.modules.hibernate.config.dynamic.TransactionManagementConfiguration;
import com.foreach.across.modules.hibernate.config.dynamic.UnitOfWorkConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers additional annotated classes based on environment property values.
 * The use of an {@link ImportSelector} is necessary as {@link AcrossHibernateModuleSettings} beans are not
 * initialized for use {@link org.springframework.context.annotation.Conditional} in annotations.
 * <p>
 * The use of the registrar allows additional modules to extend
 * {@link com.foreach.across.modules.hibernate.AbstractHibernatePackageModule} and have the optional configuration work.
 */
public class DynamicConfigurationRegistrar implements ImportSelector, EnvironmentAware
{
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER = "persistenceContextInView.handler";
	public static final String CREATE_TRANSACTION_MANAGER = "createTransactionManager";
	public static final String CREATE_UNITOFWORK_FACTORY = "createUnitOfWorkFactory";

	private Environment environment;

	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Override
	public String[] selectImports( AnnotationMetadata importingClassMetadata ) {
		List<String> imports = new ArrayList<>();

		PropertyResolver properties = new RelaxedPropertyResolver( environment, "acrossHibernate." );

		if ( properties.getProperty( CREATE_TRANSACTION_MANAGER, Boolean.class, true ) ) {
			imports.add( TransactionManagementConfiguration.class.getName() );
		}

		if ( properties.getProperty( CREATE_UNITOFWORK_FACTORY, Boolean.class, false ) ) {
			imports.add( UnitOfWorkConfiguration.class.getName() );
		}

		PersistenceContextInView persistenceContextInView = properties.getProperty(
				PERSISTENCE_CONTEXT_VIEW_HANDLER, PersistenceContextInView.class, PersistenceContextInView.FILTER
		);

		switch ( persistenceContextInView ) {
			case FILTER:
				imports.add( OpenSessionFactoryInViewFilterConfiguration.class.getName() );
				break;
			case INTERCEPTOR:
				imports.add( OpenSessionFactoryInViewInterceptorConfiguration.class.getName() );
				break;
		}

		return imports.toArray( new String[imports.size()] );
	}
}
