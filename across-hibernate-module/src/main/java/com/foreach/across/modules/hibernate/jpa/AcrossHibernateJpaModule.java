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

package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;

import javax.sql.DataSource;

/**
 * Enables JPA support using Hibernate as the implementation.
 * <p/>
 * Note that this module essentially supports the same properties as the
 * {@link com.foreach.across.modules.hibernate.AcrossHibernateModule}.  In an application where both
 * modules are configured, care should be taken that properties are set on the module directly.
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(optional = "EhcacheModule")
public class AcrossHibernateJpaModule extends AbstractHibernatePackageModule
{
	public static final String NAME = "AcrossHibernateJpaModule";

	public AcrossHibernateJpaModule() {
		setHibernateProperty( "hibernate.cache.use_second_level_cache", "false" );
		setPersistenceUnitName( getName() );
	}

	public AcrossHibernateJpaModule( DataSource dataSource ) {
		this();
		setDataSource( dataSource );
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Enables JPA support on the Across context using Hibernate as the vendor implementation.";
	}

	protected void setPersistenceUnitName( String persistenceUnitName ) {
		setProperty( AcrossHibernateJpaModuleSettings.PERSISTENCE_UNIT_NAME, persistenceUnitName );
	}
}
