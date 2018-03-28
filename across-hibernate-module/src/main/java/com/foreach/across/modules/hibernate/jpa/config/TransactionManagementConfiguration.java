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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.modules.config.EnableTransactionManagementConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;

/**
 * Configures PlatformTransactionManagers for use with @Transaction annotations.
 * If the module is configured in mixed mode, with both session factory and jpa support enabled,
 * one of both transaction managers should be configured as the primary when exposed.
 */
@Configuration
@EnableTransactionManagement(order = EnableTransactionManagementConfiguration.INTERCEPT_ORDER)
public class TransactionManagementConfiguration
{
	@Bean(name = HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Exposed
	public PlatformTransactionManager jpaTransactionManager( EntityManagerFactory entityManagerFactory, AcrossHibernateModuleSettings moduleSettings ) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager( entityManagerFactory );
		moduleSettings.getTransactionProperties().customize( transactionManager );
		return transactionManager;
	}

	@Bean
	@Exposed
	public TransactionTemplate jpaTransactionTemplate( PlatformTransactionManager jpaTransactionManager ) {
		return new TransactionTemplate( jpaTransactionManager );
	}
}
