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

package com.foreach.across.modules.debugweb.events;

import lombok.*;
import org.springframework.cache.Cache;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.time.Duration;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

/**
 * Published when general statistics for a {@link org.springframework.cache.Cache} are to be gathered.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RequiredArgsConstructor
@Getter
@Setter
public class DebugCacheStatistics<T extends Cache> implements ResolvableTypeProvider
{
	private final String cacheName;
	private final Cache cache;

	/**
	 * Number of items present in the cache.
	 */
	private Long items;

	/**
	 * Maximum number of items that can be stored in the cache.
	 */
	private Long maxItems;

	/**
	 * Cache requests that were successful.
	 */
	private Long hits;

	/**
	 * Cache requests that were not successful.
	 */
	private Long misses;

	/**
	 * Ratio between successful cache requests and total.
	 */
	private Double hitRatio;

	/**
	 * Number of cache items that have been evicted (usually to make up room for new items).
	 */
	private Long evictions;

	/**
	 * An {@link Iterable} to the entries of the cache. Can be {@code null} if details are not available.
	 * If you want to have lazy transformation of an original cache entry (for performance reasons you
	 * often do), you can use {@link #setCacheEntryIterator(Iterable, Function)}.
	 *
	 * @see #setCacheEntryIterator(Iterable, Function)
	 * @see CacheEntryIterator
	 */
	private Iterable<CacheEntry> cacheEntries;

	/**
	 * Short-hand for providing a cache entry iterator by transforming original entries into a {@link CacheEntry}
	 * whenever they are requested.
	 */
	public <V> void setCacheEntryIterator( @NonNull Iterable<V> original, @NonNull Function<V, CacheEntry> transformFunction ) {
		cacheEntries = () -> new CacheEntryIterator<>( original.iterator(), transformFunction );
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics( getClass(), ResolvableType.forInstance( cache ) );
	}

	/**
	 * Represents a single entry in the cache.
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Getter
	@RequiredArgsConstructor
	@Builder
	public static class CacheEntry
	{
		/**
		 * Key of the cache entry.
		 */
		private final Object key;

		/**
		 * Value of the cache entry. A {@code null} value means the value cannot be resolved,
		 * whereas an {@link Optional#empty()} value means the stored value is {@code null}.
		 */
		private final Optional<Object> value;

		/**
		 * How long this item has been in the cache.
		 */
		private final Duration age;

		/**
		 * How long ago this item was last accessed.
		 */
		private final Duration lastAccessed;

		/**
		 * Successful cache requests for this item.
		 */
		private final Long hits;
	}

	/**
	 * Helper which takes an original entry iterator and transforms it
	 * into a {@link CacheEntry} when the entry is requested.
	 */
	@RequiredArgsConstructor
	public static class CacheEntryIterator<U> implements Iterator<CacheEntry>
	{
		@NonNull
		private final Iterator<U> iterator;

		@NonNull
		private final Function<U, CacheEntry> transform;

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public CacheEntry next() {
			return transform.apply( iterator.next() );
		}
	}
}
