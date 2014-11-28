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
package com.foreach.across.modules.hibernate.installers;

import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.database.SchemaObject;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

import java.util.Collections;

/**
 * @author Andy Somers
 *
 * Convenience installer that creates the necessary columns for the default implementation
 * of the {@link com.foreach.across.modules.hibernate.business.Auditable} interface.
 * @see com.foreach.across.modules.hibernate.business.AuditableEntity
 */
public abstract class AuditableSchemaInstaller extends AcrossLiquibaseInstaller
{
	private final SchemaConfiguration schemaConfiguration = new SchemaConfiguration(
			Collections.<SchemaObject>emptyList() );

	protected AuditableSchemaInstaller() {
		super( "classpath:com/foreach/across/modules/hibernate/installers/AuditableSchemaInstaller.xml" );

		setSchemaConfiguration( schemaConfiguration );
	}

	protected abstract String getTableName();

	@Override
	public void install() {
		schemaConfiguration.setProperty( "table.auditable_table", getTableName() );

		super.install();
	}
}
