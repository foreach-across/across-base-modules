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
package com.foreach.across.modules.hibernate.provider;

import com.foreach.across.core.database.SchemaObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TableAliasProvider extends HibernatePackageProviderAdapter
{
	private Map<String, String> tableAlias = new HashMap<>();

	public TableAliasProvider() {
	}

	public TableAliasProvider( Collection<SchemaObject> tables ) {
		for ( SchemaObject table : tables ) {
			if ( !StringUtils.equals( table.getOriginalName(), table.getCurrentName() ) ) {
				tableAlias.put( table.getOriginalName(), table.getCurrentName() );
			}
		}
	}

	public TableAliasProvider( Map<String, String> tableAlias ) {
		this.tableAlias = tableAlias;
	}

	public void addAlias( String original, String name ) {
		tableAlias.put( original, name );
	}

	@Override
	public Map<String, String> getTableAliases() {
		return tableAlias;
	}
}
