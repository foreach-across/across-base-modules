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

public class HibernatePackageProviderComposite implements HibernatePackageProvider
{
	private HibernatePackageProvider[] providers;

	public HibernatePackageProviderComposite( HibernatePackageProvider... providers ) {
		this.providers = providers;
	}

	@Override
	public String[] getPackagesToScan() {
		Collection<String> packagesToScan = new HashSet<String>();
		for ( HibernatePackageProvider provider : providers ) {
			packagesToScan.addAll( Arrays.asList( provider.getPackagesToScan() ) );
		}

		return packagesToScan.toArray( new String[packagesToScan.size()] );
	}

	@Override
	public Class<?>[] getAnnotatedClasses() {
		Collection<Class<?>> annotatedClasses = new HashSet<Class<?>>();
		for ( HibernatePackageProvider provider : providers ) {
			annotatedClasses.addAll( Arrays.asList( provider.getAnnotatedClasses() ) );
		}

		return annotatedClasses.toArray( new Class<?>[annotatedClasses.size()] );
	}

	@Override
	public String[] getMappingResources() {
		Collection<String> mappingResources = new HashSet<String>();
		for ( HibernatePackageProvider provider : providers ) {
			mappingResources.addAll( Arrays.asList( provider.getMappingResources() ) );
		}

		return mappingResources.toArray( new String[mappingResources.size()] );
	}

	@Override
	public Map<String, String> getTableAliases() {
		Map<String, String> tableAliases = new HashMap<>();
		for ( HibernatePackageProvider provider : providers ) {
			tableAliases.putAll( provider.getTableAliases() );
		}

		return tableAliases;
	}
}
