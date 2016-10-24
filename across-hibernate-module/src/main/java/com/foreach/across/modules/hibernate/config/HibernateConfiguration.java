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
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.Map;
import java.util.Properties;

/**
 * Configures a standard SessionFactory.
 *
 * @see com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration
 * @see com.foreach.across.modules.hibernate.config.DynamicConfigurationRegistrar
 */
@Configuration
@Import(DynamicConfigurationRegistrar.class)
public class HibernateConfiguration
{
	public static final String TRANSACTION_MANAGER = "transactionManager";
	public static final String SESSION_HOLDER = "hibernateSessionHolder";

	private static final Logger LOG = LoggerFactory.getLogger( HibernateConfiguration.class );

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossHibernateModule module;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossHibernateModuleSettings settings;

	@Autowired
	private org.springframework.core.env.Environment environment;

	@Bean
	@Exposed
	public LocalSessionFactoryBean sessionFactory( HibernatePackage hibernatePackage ) {
		String version = org.hibernate.Version.getVersionString();
		Map hibernateProperties = settings.getHibernateProperties();

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource( module.getDataSource() );
		sessionFactory.setPackagesToScan( hibernatePackage.getPackagesToScan() );
		sessionFactory.setMappingResources( hibernatePackage.getMappingResources() );

		Properties propertiesToSet = new Properties();
		propertiesToSet.putAll( hibernateProperties );

		sessionFactory.setHibernateProperties( propertiesToSet );

		return sessionFactory;
	}

	@Bean(name = SESSION_HOLDER)
	@Exposed
	public HibernateSessionHolder hibernateSessionHolder() {
		return new HibernateSessionHolderImpl();
	}

	@Bean
	@Exposed
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Event
	@SuppressWarnings("unused")
	protected void registerClientModuleRepositoryInterceptors( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		if ( settings.isRegisterRepositoryInterceptor() ) {
			LOG.trace( "Enabling BasicRepositoryInterceptor support in module {}",
			           beforeBootstrapEvent.getModule().getName() );
			beforeBootstrapEvent.addApplicationContextConfigurers(
					new AnnotatedClassConfigurer( ModuleBasicRepositoryInterceptorConfiguration.class )
			);
		}
	}
}
