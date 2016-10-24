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
package com.foreach.across.modules.hibernate.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Map;

public class TableAliasNamingStrategy extends PhysicalNamingStrategyStandardImpl
{
	private final Map<String, String> aliasMap;

	public TableAliasNamingStrategy( Map<String, String> aliasMap ) {
		this.aliasMap = aliasMap;
	}

	@Override
	public Identifier toPhysicalTableName( Identifier name, JdbcEnvironment context ) {
		return new Identifier( alias( name.getText() ), name.isQuoted() );
	}

	private String alias( String tableName ) {
		if ( aliasMap.containsKey( tableName ) ) {
			return aliasMap.get( tableName );
		}

		return tableName;
	}
}

