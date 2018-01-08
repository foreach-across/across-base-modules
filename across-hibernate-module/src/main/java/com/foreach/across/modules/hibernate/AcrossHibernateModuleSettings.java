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

import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
@SuppressWarnings("all")
@ConfigurationProperties(prefix = "acrossHibernate")
public class AcrossHibernateModuleSettings
{
	public static final String HIBERNATE_PROPERTIES = "acrossHibernate.hibernateProperties";
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER = "acrossHibernate.persistenceContextInView.handler";
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER_ORDER =
			"acrossHibernate.persistenceContextInView.order";
	public static final String CREATE_TRANSACTION_MANAGER = "acrossHibernate.createTransactionManager";
	public static final String CREATE_UNITOFWORK_FACTORY = "acrossHibernate.createUnitOfWorkFactory";
	public static final String REGISTER_REPOSITORY_INTERCEPTOR = "acrossHibernate.registerRepositoryInterceptor";

	private final PersistenceContextInViewProperties persistenceContextInView =
			new PersistenceContextInViewProperties();

	/**
	 * Map of Hibernate specific properties.
	 */
	private Map<String, String> hibernateProperties = new HashMap<>();

	/**
	 * Should a TransactionManager bean be created.  If true this will also enable support for
	 * {@link org.springframework.transaction.annotation.Transactional} in all modules bootstrapping later.
	 */
	private boolean createTransactionManager = true;

	/**
	 * Should a UnitOfWorkFactory be created.
	 */
	private boolean createUnitOfWorkFactory = false;

	/**
	 * Should common Repository implementations in modules automatically be intercepted.
	 */
	private boolean registerRepositoryInterceptor = true;

	/**
	 * Should the dynamic application module be scanned automatically for entities.
	 */
	@Getter
	@Setter
	private boolean autoScanApplicationModule = false;

	public void setHibernateProperties( Map<String, String> hibernateProperties ) {
		this.hibernateProperties = hibernateProperties;
	}

	public void setCreateTransactionManager( boolean createTransactionManager ) {
		this.createTransactionManager = createTransactionManager;
	}

	public void setCreateUnitOfWorkFactory( boolean createUnitOfWorkFactory ) {
		this.createUnitOfWorkFactory = createUnitOfWorkFactory;
	}

	public void setRegisterRepositoryInterceptor( boolean registerRepositoryInterceptor ) {
		this.registerRepositoryInterceptor = registerRepositoryInterceptor;
	}

	public PersistenceContextInViewProperties getPersistenceContextInView() {
		return persistenceContextInView;
	}

	public Map<String, String> getHibernateProperties() {
		return hibernateProperties;
	}

	public boolean isCreateTransactionManager() {
		return createTransactionManager;
	}

	public boolean isCreateUnitOfWorkFactory() {
		return createUnitOfWorkFactory;
	}

	public boolean isRegisterRepositoryInterceptor() {
		return registerRepositoryInterceptor;
	}

	public static class PersistenceContextInViewProperties
	{
		/**
		 * If a view layer is enabled, should an open session/entity manager be created for the entire
		 * request by using either a filter or an interceptor.
		 */
		private PersistenceContextInView handler = PersistenceContextInView.FILTER;

		/**
		 * Configure the order of the persistence context view handler (if created).
		 */
		private int order = Ordered.HIGHEST_PRECEDENCE + 1;

		public PersistenceContextInView getHandler() {
			return handler;
		}

		public void setHandler( PersistenceContextInView handler ) {
			this.handler = handler;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder( int order ) {
			this.order = order;
		}
	}
}
