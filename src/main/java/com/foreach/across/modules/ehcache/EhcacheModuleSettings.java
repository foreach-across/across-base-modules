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
package com.foreach.across.modules.ehcache;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class EhcacheModuleSettings extends AcrossModuleSettings
{
	public static final String CONFIGURATION_RESOURCE = "ehcache.configuration.resource";
	public static final String CONFIGURATION = "ehcache.configuration";

	public static final String CACHE_MANAGER_IS_SHARED = "ehcache.cacheManager.shared";
	public static final String CACHE_MANAGER_NAME = "ehcache.cacheManager.name";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( CONFIGURATION_RESOURCE, Resource.class, new ClassPathResource( "ehcache.xml" ),
		                   "Resource representing the ehcache XML configuration file (defaults to: ehcache.xml)" );
		registry.register( CONFIGURATION, Object.class,
		                   null,
		                   "Configuration class instance or class name" );
		registry.register( CACHE_MANAGER_IS_SHARED, Boolean.class, true,
		                   "Should the created CacheManager be shared at the ClassLoader level." );
		registry.register( CACHE_MANAGER_NAME, String.class, null,
		                   "Name of the CacheManager instance." );
	}

	public Resource getConfigurationResource() {
		return getProperty( CONFIGURATION_RESOURCE, Resource.class );
	}

	public Object getConfiguration() {
		return getProperty( CONFIGURATION, Object.class );
	}

	public boolean isSharedCacheManager() {
		return getProperty( CACHE_MANAGER_IS_SHARED, Boolean.class );
	}

	public String getCachemanagerName() {
		return getProperty( CACHE_MANAGER_NAME, String.class );
	}
}