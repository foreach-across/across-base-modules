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

package test.debugweb.application;

import com.foreach.across.core.cache.AcrossCompositeCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
class TouchCache
{
	@Autowired
	public void touchCache( CacheManager cacheManager ) {
		cacheManager.getCache( "some cache" ).put( "one", 123L );
	}

	@Autowired
	public void registerCache( AcrossCompositeCacheManager cacheManager ) {
		ConcurrentMapCacheManager cacheManager2 = new ConcurrentMapCacheManager( "concurrent-map" );
		cacheManager.addCacheManager( cacheManager2 );
		cacheManager.getCache( "concurrent-map" ).put( "actual", "value" );
		cacheManager.getCache( "concurrent-map" ).put( "nullValue", null );
	}

	@Bean
	public CacheManager otherCacheManager() {
		return new ConcurrentMapCacheManager();
	}
}
