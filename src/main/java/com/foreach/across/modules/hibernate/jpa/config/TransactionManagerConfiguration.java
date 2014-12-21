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
package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.context.configurer.TransactionManagementConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

/**
 * Configures PlatformTransactionManagers for use with @Transaction annotations.
 * If the module is configured in mixed mode, with both session factory and jpa support enabled,
 * on of both transaction managers should be configured as the primary when exposed.
 */
@Configuration
@AcrossEventHandler
@AcrossCondition("settings.createTransactionManager")
@EnableTransactionManagement
public class TransactionManagerConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( TransactionManagerConfiguration.class );

	@Bean(name = HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Exposed
	public PlatformTransactionManager jpaTransactionManager( EntityManagerFactory entityManagerFactory ) {
		return new JpaTransactionManager( entityManagerFactory );
	}

	@Event
	@SuppressWarnings("unused")
	protected void registerClientModuleTransactionSupport( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		LOG.trace( "Enabling @Transaction support in module {}", beforeBootstrapEvent.getModule().getName() );
		beforeBootstrapEvent.addApplicationContextConfigurers( new TransactionManagementConfigurer() );
	}
}
