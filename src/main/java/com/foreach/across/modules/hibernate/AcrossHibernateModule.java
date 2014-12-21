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

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;

import javax.sql.DataSource;

/**
 * Activates hibernate support on all modules implementing HasHibernatePackageProvider
 * Will also activate Transactional support on the modules.
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(optional = "EhcacheModule")
public class AcrossHibernateModule extends AbstractHibernatePackageModule
{
	public static final String NAME = "AcrossHibernateModule";

	public AcrossHibernateModule() {
	}

	public AcrossHibernateModule( DataSource dataSource ) {
		setDataSource( dataSource );
	}

	/**
	 * @return Name of this module.  The spring bean should also be using this name.
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * @return Description of the content of this module.
	 */
	@Override
	public String getDescription() {
		return "Enables Hibernate support on the context and provides an exposed SessionFactory bean.";
	}
}
