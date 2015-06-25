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
package com.foreach.across.modules.hibernate;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class AcrossHibernateModuleSettings extends AcrossModuleSettings
{
	public static final String HIBERNATE_PROPERTIES = "acrossHibernate.hibernateProperties";
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER = "acrossHibernate.persistenceContextInView.handler";
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER_ORDER =
			"acrossHibernate.persistenceContextInView.order";
	public static final String CREATE_TRANSACTION_MANAGER = "acrossHibernate.transactionManager";
	public static final String CREATE_UNITOFWORK_FACTORY = "acrossHibernate.unitOfWorkFactory";
	public static final String REGISTER_REPOSITORY_INTERCEPTOR = "acrossHibernate.registerBasicRepositoryInterceptor";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {

		registry.register( CREATE_TRANSACTION_MANAGER, Boolean.class, true,
		                   "Should a TransactionManager bean be created.  If true this will also enable support for " +
				                   "@Transaction in all modules bootstrapping later." );
		registry.register( CREATE_UNITOFWORK_FACTORY, Boolean.class, false,
		                   "Should a UnitOfWorkFactory bean be created." );

		registry.register( PERSISTENCE_CONTEXT_VIEW_HANDLER, PersistenceContextInView.class,
		                   PersistenceContextInView.NONE,
		                   "If a view layer is enabled, should an open session/entity manager be created for the entire " +
				                   "request by using either a filter or an interceptor." );
		registry.register( PERSISTENCE_CONTEXT_VIEW_HANDLER_ORDER, Integer.class, Ordered.HIGHEST_PRECEDENCE + 1,
		                   "Configure the order of the persistence context view handler (if create)." );
		registry.register( REGISTER_REPOSITORY_INTERCEPTOR, Boolean.class, true,
		                   "Should BasicRepository implementations in modules automatically be intercepted.");
	}

	public boolean isCreateTransactionManager() {
		return getProperty( CREATE_TRANSACTION_MANAGER, Boolean.class );
	}

	public boolean isCreateUnitOfWorkFactory() {
		return getProperty( CREATE_UNITOFWORK_FACTORY, Boolean.class );
	}

	public boolean isRegisterRepositoryInterceptor() {
		return getProperty( REGISTER_REPOSITORY_INTERCEPTOR, Boolean.class );
	}

	public PersistenceContextInView getPersistenceContextInView() {
		return getProperty( PERSISTENCE_CONTEXT_VIEW_HANDLER, PersistenceContextInView.class );
	}

	public int getPersistenceContextInViewOrder() {
		return getProperty( PERSISTENCE_CONTEXT_VIEW_HANDLER_ORDER, Integer.class );
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getHibernateProperties() {
		return getProperty( HIBERNATE_PROPERTIES, Map.class, new HashMap<String, Object>() );
	}
}
