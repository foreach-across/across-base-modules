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

import net.sf.ehcache.config.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@ConfigurationProperties("ehcacheModule")
public class EhcacheModuleSettings
{
	public static final String CONFIGURATION_RESOURCE = "ehcacheModule.configurationResource";
	public static final String CONFIGURATION_OBJECT = "ehcacheModule.configurationObject";

	public static final String CACHE_MANAGER_IS_SHARED = "ehcacheModule.cacheManagerIsShared";
	public static final String CACHE_MANAGER_NAME = "ehcacheModule.cacheManagerName";

	/**
	 * Resource representing the ehcache XML configuration file (defaults to: ehcache.xml).
	 */
	private Resource configurationResource = new ClassPathResource( "ehcache.xml" );

	/**
	 * Configuration class instance.
	 */
	private Configuration configurationObject;

	/**
	 * Should the created CacheManager be shared at the ClassLoader level.
	 */
	private Boolean cacheManagerIsShared = true;

	/**
	 * Name of the CacheManager instance.
	 */
	private String cacheManagerName;

	public Resource getConfigurationResource() {
		return configurationResource;
	}

	public void setConfigurationResource( Resource configurationResource ) {
		this.configurationResource = configurationResource;
	}

	public Configuration getConfigurationObject() {
		return configurationObject;
	}

	public void setConfigurationObject( Configuration configurationObject ) {
		this.configurationObject = configurationObject;
	}

	public Boolean getCacheManagerIsShared() {
		return cacheManagerIsShared;
	}

	public void setCacheManagerIsShared( Boolean cacheManagerIsShared ) {
		this.cacheManagerIsShared = cacheManagerIsShared;
	}

	public String getCacheManagerName() {
		return cacheManagerName;
	}

	public void setCacheManagerName( String cacheManagerName ) {
		this.cacheManagerName = cacheManagerName;
	}
}
