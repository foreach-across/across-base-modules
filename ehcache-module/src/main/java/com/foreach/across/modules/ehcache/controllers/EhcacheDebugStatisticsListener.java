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

package com.foreach.across.modules.ehcache.controllers;

import com.foreach.across.modules.debugweb.events.DebugCacheStatistics;
import com.foreach.across.modules.debugweb.events.DebugCacheStatistics.CacheEntry;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Resolves the Ehcache specific statistics for a Spring wrapped cache.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
class EhcacheDebugStatisticsListener
{
	@EventListener
	@SuppressWarnings("unchecked")
	public void loadCacheStatistics( DebugCacheStatistics<EhCacheCache> statistics ) {
		Cache cache = (Cache) statistics.getCache().getNativeCache();
		statistics.setItems( Integer.valueOf( cache.getSize() ).longValue() );
		statistics.setMaxItems( cache.getCacheConfiguration().getMaxEntriesLocalHeap() );
		StatisticsGateway ehcacheStatistics = cache.getStatistics();
		statistics.setHits( ehcacheStatistics.cacheHitCount() );
		statistics.setMisses( ehcacheStatistics.cacheMissCount() );

		double accessCount = ehcacheStatistics.cacheHitCount() + ehcacheStatistics.cacheMissCount();
		if ( accessCount > 0 ) {
			statistics.setHitRatio( ( ehcacheStatistics.cacheHitCount() * 1.0 / accessCount ) * 100 );
		}

		statistics.setEvictions( ehcacheStatistics.cacheEvictedCount() );

		statistics.setCacheEntryIterator(
				cache.getKeys(),
				key -> {
					Element cacheElement = cache.getQuiet( key );

					if ( cacheElement != null && !cacheElement.isExpired() ) {
						long age = System.currentTimeMillis() - cacheElement.getLatestOfCreationAndUpdateTime();
						long accessed = System.currentTimeMillis() - cacheElement.getLastAccessTime();

						return CacheEntry.builder()
						                 .key( key )
						                 .value( Optional.ofNullable( cacheElement.getObjectValue() ) )
						                 .age( Duration.ofMillis( age ) )
						                 .lastAccessed( Duration.ofMillis( accessed ) )
						                 .hits( cacheElement.getHitCount() )
						                 .build();
					}

					return null;
				}
		);
	}
}
