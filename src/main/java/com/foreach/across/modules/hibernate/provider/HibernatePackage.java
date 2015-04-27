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

import java.util.*;

/**
 * Represents a set of configuration items for Hibernate.
 */
public class HibernatePackage implements HibernatePackageRegistry
{
	private final String name;

	private Collection<String> packagesToScan = new HashSet<String>();
	private Collection<String> mappingResources = new HashSet<String>();
	private Map<String, String> tableAliases = new HashMap<>();

	public HibernatePackage( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String[] getPackagesToScan() {
		return packagesToScan.toArray( new String[packagesToScan.size()] );
	}

	public String[] getMappingResources() {
		return mappingResources.toArray( new String[mappingResources.size()] );
	}

	public Map<String, String> getTableAliases() {
		return tableAliases;
	}

	public void addPackageToScan( String... packageToScan ) {
		packagesToScan.addAll( Arrays.asList( packageToScan ) );
	}

	@Override
	public void addPackageToScan( Class... classes ) {
		for ( Class c : classes ) {
			addPackageToScan( c.getPackage().getName() );
		}
	}

	public void addMappingResource( String... mappingResource ) {
		mappingResources.addAll( Arrays.asList( mappingResource ) );
	}

	public void add( HibernatePackageProvider provider ) {
		packagesToScan.addAll( Arrays.asList( provider.getPackagesToScan() ) );
		mappingResources.addAll( Arrays.asList( provider.getMappingResources() ) );
		tableAliases.putAll( provider.getTableAliases() );
	}
}
