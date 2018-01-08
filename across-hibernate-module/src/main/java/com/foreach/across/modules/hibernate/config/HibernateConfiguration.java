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

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.jpa.config.JpaModulePropertiesRegistrar;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.strategy.TableAliasNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * Configures a standard SessionFactory.
 *
 * @see com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration
 * @see com.foreach.across.modules.hibernate.config.DynamicConfigurationRegistrar
 */
@Configuration
@Import({ JpaModulePropertiesRegistrar.class, DynamicConfigurationRegistrar.class, HibernatePackageBuilder.class })
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

	@Autowired(required = false)
	@Qualifier(AcrossContext.DATASOURCE)
	private DataSource acrossDataSource;

	@Bean
	@Exposed
	public LocalSessionFactoryBean sessionFactory( HibernatePackage hibernatePackage ) {
		Map hibernateProperties = settings.getHibernateProperties();

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource( retrieveDataSource() );
		sessionFactory.setPackagesToScan( hibernatePackage.getPackagesToScan() );
		sessionFactory.setMappingResources( hibernatePackage.getMappingResources() );

		Map<String, String> tableAliases = hibernatePackage.getTableAliases();

		if ( !tableAliases.isEmpty() ) {
			sessionFactory.setPhysicalNamingStrategy( new TableAliasNamingStrategy( tableAliases ) );
		}

		Properties propertiesToSet = new Properties();
		propertiesToSet.putAll( hibernateProperties );

		sessionFactory.setHibernateProperties( propertiesToSet );

		return sessionFactory;
	}

	private DataSource retrieveDataSource() {
		DataSource moduleDataSource = module.getDataSource();

		if ( moduleDataSource == null ) {
			LOG.debug( "No module datasource specified - falling back to default Across datasource" );
			module.setDataSource( acrossDataSource );
			return acrossDataSource;
		}
		else {
			return moduleDataSource;
		}
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

	@EventListener
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
