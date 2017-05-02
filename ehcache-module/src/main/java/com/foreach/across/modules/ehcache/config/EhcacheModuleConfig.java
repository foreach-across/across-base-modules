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

package com.foreach.across.modules.ehcache.config;

import com.foreach.across.core.cache.AcrossCompositeCacheManager;
import com.foreach.across.modules.ehcache.EhcacheModuleSettings;
import com.foreach.across.modules.ehcache.handlers.RegisterClientModuleConfigHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the cache manager instance that is shared between all modules.
 */
@Configuration
@ComponentScan("com.foreach.across.modules.ehcache.controllers")
public class EhcacheModuleConfig
{
	@Autowired
	private AcrossCompositeCacheManager acrossCompositeCacheManager;

	@Autowired
	private EhcacheModuleSettings ehcacheModuleSettings;

	@Bean
	public AcrossEhCacheManagerFactoryBean acrossEhCacheManagerFactoryBean() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		AcrossEhCacheManagerFactoryBean ehCacheManagerFactoryBean = new AcrossEhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setCacheManagerName( ehcacheModuleSettings.getCacheManagerName() );
		ehCacheManagerFactoryBean.setShared( ehcacheModuleSettings.getCacheManagerIsShared() );

		net.sf.ehcache.config.Configuration configurationObject = ehcacheModuleSettings.getConfigurationObject();
		if ( configurationObject != null ) {
			ehCacheManagerFactoryBean.setConfiguration( configurationObject );
		}
		else {
			ehCacheManagerFactoryBean.setConfigLocation( ehcacheModuleSettings.getConfigurationResource() );
		}

		return ehCacheManagerFactoryBean;
	}

	@Bean
	public CacheManager ehCacheManager( net.sf.ehcache.CacheManager ehCacheCacheManager ) {
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager( ehCacheCacheManager );
		// add ourselves to the global across cache manager
		acrossCompositeCacheManager.addCacheManager( cacheManager );
		return cacheManager;
	}

	@Bean
	public RegisterClientModuleConfigHandler registerClientModuleConfigHandler() {
		return new RegisterClientModuleConfigHandler();
	}
}