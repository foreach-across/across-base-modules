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

package com.foreach.across.modules.debugweb.controllers.cache;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.debugweb.events.DebugCacheStatistics;
import com.foreach.across.modules.debugweb.mvc.DebugMenu;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.NoOpCache;
import org.springframework.cache.support.NullValue;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@DebugWebController
@RequiredArgsConstructor
public class CacheController
{
	private final AcrossContextBeanRegistry beanRegistry;
	private final ApplicationEventPublisher eventPublisher;

	@EventListener
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .group( "/cache", "Cache browser" ).and()
		     .item( "/cache/cacheManagers", "Cache managers", "/cache/cacheManagers" ).order(
				Ordered.HIGHEST_PRECEDENCE );
	}

	@GetMapping("/cache/cacheManagers")
	public String cacheManagers( Model model,
	                             @RequestParam(name = "cacheManager", required = false) String cacheManagerName,
	                             @RequestParam(name = "cleared", required = false) Integer cachesCleared ) {
		Map<String, CacheManager> cacheManagerBeans = beanRegistry.getBeansOfTypeAsMap( CacheManager.class, true );

		List<CacheManagerInfo> cacheManagers = cacheManagerBeans.entrySet()
		                                                        .stream()
		                                                        .map( e -> new CacheManagerInfo( e.getKey(), e.getValue() ) )
		                                                        .collect( Collectors.toList() );

		CacheManagerInfo cacheManager = cacheManagers.get( 0 );
		if ( !StringUtils.isEmpty( cacheManagerName ) ) {
			cacheManager = cacheManagers.stream().filter( c -> c.getName().equals( cacheManagerName ) ).findFirst().orElse( cacheManager );
		}

		model.addAttribute( "cacheManagers", cacheManagers );
		model.addAttribute( "cacheManager", cacheManager );
		model.addAttribute( "cachesCleared", cachesCleared );

		return "th/debugweb/cache/cacheManagers";
	}

	@GetMapping(value = "/cache/cacheManagers/cache")
	public String cacheDetail( @RequestParam("cacheManager") String cacheManagerName,
	                           @RequestParam("cache") String cacheName,
	                           Model model,
	                           DebugMenu debugMenu,
	                           @RequestParam(name = "cleared", required = false) Integer cachesCleared ) {
		Function<Duration, String> durationFormat = duration -> duration != null
				? DurationFormatUtils.formatDurationHMS( duration.toMillis() ) : "";
		model.addAttribute( "durationFormat", durationFormat );

		Map<String, CacheManager> cacheManagerBeans = beanRegistry.getBeansOfTypeAsMap( CacheManager.class, true );

		CacheManager cacheManager = cacheManagerBeans.get( cacheManagerName );
		Assert.notNull( cacheManager, "No such CacheManager: " + cacheManagerName );
		debugMenu.getLowestSelectedItem().addItem( "/cacheManager", cacheManagerName ).setSelected( true );

		Cache cache = cacheManager.getCache( cacheName );
		Assert.notNull( cache, "No such Cache: " + cacheName );

		model.addAttribute( "cachesCleared", cachesCleared );
		model.addAttribute( "cacheManager", new CacheManagerInfo( cacheManagerName, cacheManager ) );
		model.addAttribute( "cache", new CacheInfo( cacheName, cache ) );

		return "th/debugweb/cache/cacheDetail";
	}

	@GetMapping(value = "/cache/cacheManagers/clear", params = { "cacheManager" })
	public String clearCache( @RequestParam(name = "cacheManager") String cacheManagerName ) {
		Map<String, CacheManager> beansOfTypeAsMap = beanRegistry.getBeansOfTypeAsMap( CacheManager.class, true );

		long cachesCleared = Optional.ofNullable( beansOfTypeAsMap.get( cacheManagerName ) )
		                             .map(
				                             cacheManager -> cacheManager.getCacheNames()
				                                                         .stream()
				                                                         .map( cacheManager::getCache )
				                                                         .filter( Objects::nonNull )
				                                                         .peek( Cache::clear )
				                                                         .count()
		                             )
		                             .orElse( 0L );

		return "redirect:" + UriComponentsBuilder.newInstance()
		                                         .path( "@debugWeb:/cache/cacheManagers" )
		                                         .queryParam( "cacheManager", cacheManagerName )
		                                         .queryParam( "cleared", cachesCleared )
		                                         .toUriString();
	}

	@GetMapping(value = "/cache/cacheManagers/clear", params = { "cacheManager", "cache" })
	public String clearCache( @RequestParam("cacheManager") String cacheManagerName,
	                          @RequestParam("cache") String cacheName,
	                          @RequestParam(required = false, defaultValue = "false") boolean redirectToManager ) {
		CacheManager cacheManager = beanRegistry.getBeansOfTypeAsMap( CacheManager.class, true ).get( cacheManagerName );

		int cachesCleared = 0;

		if ( cacheManager != null ) {
			Cache cache = cacheManager.getCache( cacheName );
			if ( cache != null ) {
				cache.clear();
				cachesCleared = 1;
			}
		}

		if ( redirectToManager ) {
			return "redirect:" + UriComponentsBuilder.newInstance()
			                                         .path( "@debugWeb:/cache/cacheManagers" )
			                                         .queryParam( "cacheManager", cacheManagerName )
			                                         .queryParam( "cleared", cachesCleared )
			                                         .toUriString();
		}

		return "redirect:" + UriComponentsBuilder.newInstance()
		                                         .path( "@debugWeb:/cache/cacheManagers/cache" )
		                                         .queryParam( "cacheManager", cacheManagerName )
		                                         .queryParam( "cache", cacheName )
		                                         .queryParam( "cleared", cachesCleared )
		                                         .toUriString();
	}

	@EventListener
	public void noopCacheStatistics( DebugCacheStatistics<NoOpCache> cacheStats ) {
		cacheStats.setItems( 0L );
		cacheStats.setHitRatio( 0.0 );
		cacheStats.setMaxItems( 0L );
		cacheStats.setEvictions( 0L );
		cacheStats.setHits( 0L );
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void concurrentMapCache( DebugCacheStatistics<ConcurrentMapCache> cacheStats ) {
		Map<Object, Object> nativeCache = (Map<Object, Object>) cacheStats.getCache().getNativeCache();
		cacheStats.setItems( Integer.valueOf( nativeCache.size() ).longValue() );
		cacheStats.setCacheEntryIterator(
				nativeCache.entrySet(),
				e -> {
					Object value = e.getValue();
					Optional optionalValue = value instanceof NullValue ? Optional.empty() : Optional.ofNullable( value );
					return DebugCacheStatistics.CacheEntry.builder().key( e.getKey() ).value( optionalValue ).age( Duration.ofSeconds( 1000 ) ).build();
				}
		);
	}

	@Getter
	@RequiredArgsConstructor
	class CacheManagerInfo
	{
		private final String name;
		private final CacheManager cacheManager;

		private Collection<CacheInfo> caches;

		public Collection<CacheInfo> getCaches() {
			if ( caches == null ) {
				caches = cacheManager.getCacheNames()
				                     .stream()
				                     .sorted( Comparator.comparing( String::toLowerCase ) )
				                     .map( cacheName -> new CacheInfo( cacheName, cacheManager.getCache( cacheName ) ) )
				                     .collect( Collectors.toList() );
			}
			return caches;
		}
	}

	@Getter
	@RequiredArgsConstructor
	class CacheInfo
	{
		private final String name;
		private final Cache cache;

		private DebugCacheStatistics statistics;

		public Iterable<DebugCacheStatistics.CacheEntry> getCacheEntries() {
			return getStats().getCacheEntries();
		}

		public DebugCacheStatistics<?> getStats() {
			if ( statistics == null ) {
				statistics = new DebugCacheStatistics( name, cache );
				eventPublisher.publishEvent( statistics );
			}
			return statistics;
		}
	}
}
