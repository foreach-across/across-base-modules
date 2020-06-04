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
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AcrossHibernateModuleSettings extends JpaProperties
{
	public static final String HIBERNATE_PROPERTIES = "across-hibernate.hibernate-properties";
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER = "across-hibernate.persistence-context-in-view.handler";
	public static final String PERSISTENCE_CONTEXT_VIEW_HANDLER_ORDER = "across-hibernate.persistence-context-in-view.order";
	public static final String CREATE_UNITOFWORK_FACTORY = "across-hibernate.create-unit-of-work-factory";
	public static final String REGISTER_REPOSITORY_INTERCEPTOR = "across-hibernate.register-repository-interceptor";

	private TransactionProperties transactionProperties = new TransactionProperties();
	private HibernateProperties hibernate = new HibernateProperties();
	private ApplicationModule applicationModule = new ApplicationModule();
	private PersistenceContextInViewProperties persistenceContextInView = new PersistenceContextInViewProperties();

	/**
	 * Name of the dataSource bean that should be resolved and used for the entity manager.
	 */
	private String dataSource;

	/**
	 * Map of Hibernate specific properties.
	 *
	 * @deprecated use the more generic {@link #setProperties(Map)}
	 */
	private Map<String, String> hibernateProperties = new HashMap<>();

	/**
	 * Should a UnitOfWorkFactory be created. This allows you manually manage a session.
	 */
	private boolean createUnitOfWorkFactory = false;

	/**
	 * Should common Repository implementations in modules automatically be intercepted.
	 * This will enable support for {@link com.foreach.across.modules.hibernate.aop.EntityInterceptor} on the entities
	 * managed by those repositories.
	 */
	private boolean registerRepositoryInterceptor = false;

	public AcrossHibernateModuleSettings() {
		setOpenInView( true );
	}

	public boolean isOpenInView() {
		return Boolean.TRUE.equals( getOpenInView() );
	}

	/**
	 * Get the merged set of Hibernate properties for the datasource.
	 *
	 * @param hibernateSettings to detect default properties from
	 * @return merged properties set
	 */
	public Map<String, Object> getHibernateProperties( HibernateSettings hibernateSettings ) {
		Map<String, Object> hibernateProperties = getHibernate().determineHibernateProperties( getProperties(), hibernateSettings );
		hibernateProperties.putAll( getHibernateProperties() );
		return hibernateProperties;
	}

	@Data
	public static class ApplicationModule
	{
		/**
		 * Is entity scanning of the application module enabled?
		 */
		private boolean entityScan = false;

		/**
		 * Is Spring data repository scanning of the application module enabled?
		 */
		private boolean repositoryScan = false;
	}

	@Data
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
	}
}
