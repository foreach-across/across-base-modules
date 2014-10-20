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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Dummy configuration class that enables caching in any ApplicationContext where it is loaded.
 */
@Configuration
@EnableCaching(order = EhcacheClientModuleConfig.INTERCEPT_ORDER)
public class EhcacheClientModuleConfig
{
	/**
	 * Order for the AOP interceptor.
	 */
	public static final int INTERCEPT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

	@Autowired
	private AcrossCompositeCacheManager cacheManager;

	@Bean
	public CachingConfigurer cachingConfigurer() {
		return new CachingConfigurer()
		{
			@Override
			public CacheManager cacheManager() {
				return cacheManager;
			}

			@Override
			public KeyGenerator keyGenerator() {
				return new SimpleKeyGenerator();
			}
		};
	}
}
