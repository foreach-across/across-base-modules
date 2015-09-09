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

import org.hibernate.cfg.ImprovedNamingStrategy;

import java.util.Map;

public class TableAliasNamingStrategy extends ImprovedNamingStrategy
{
	private final Map<String, String> aliasMap;

	public TableAliasNamingStrategy( Map<String, String> aliasMap ) {
		this.aliasMap = aliasMap;
	}

	@Override
	public String classToTableName( String className ) {
		return alias( super.classToTableName( className ) );
	}

	@Override
	public String tableName( String tableName ) {
		return alias( super.tableName( tableName ) );
	}

	private String alias( String tableName ) {
		if ( aliasMap.containsKey( tableName ) ) {
			return aliasMap.get( tableName );
		}

		return tableName;
	}
}
