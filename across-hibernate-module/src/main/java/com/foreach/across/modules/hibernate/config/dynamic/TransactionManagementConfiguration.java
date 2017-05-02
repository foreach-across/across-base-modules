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

package com.foreach.across.modules.hibernate.config.dynamic;

import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.context.configurer.TransactionManagementConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.config.HibernateConfiguration;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author arne
 */
@EnableTransactionManagement
public class TransactionManagementConfiguration
{
	public static final Logger LOG = LoggerFactory.getLogger( TransactionManagementConfiguration.class );

	@Bean(name = HibernateConfiguration.TRANSACTION_MANAGER)
	@Exposed
	public HibernateTransactionManager transactionManager( SessionFactory sessionFactory ) {
		return new HibernateTransactionManager( sessionFactory );
	}

	@Event
	@SuppressWarnings("unused")
	protected void registerClientModuleTransactionSupport( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		LOG.trace( "Enabling @Transaction support in module {}",
		           beforeBootstrapEvent.getModule().getName() );
		beforeBootstrapEvent.addApplicationContextConfigurers( new TransactionManagementConfigurer() );
	}
}
