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
package com.foreach.across.modules.metrics;

import com.codahale.metrics.*;
import org.apache.commons.lang3.NotImplementedException;

import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class InMemoryReporter extends ScheduledReporter
{
	/**
	 * Returns a new {@link Builder} for {@link ConsoleReporter}.
	 *
	 * @param registry the registry to report
	 * @return a {@link Builder} instance for a {@link ConsoleReporter}
	 */
	public static Builder forRegistry(MetricRegistry registry) {
		return new Builder(registry);
	}

	public static class Builder
	{
		private final MetricRegistry registry;
		private TimeUnit rateUnit;
		private TimeUnit durationUnit;
		private MetricFilter filter;

		private Builder( MetricRegistry registry ) {
			this.registry = registry;
			this.rateUnit = TimeUnit.SECONDS;
			this.durationUnit = TimeUnit.MILLISECONDS;
			this.filter = MetricFilter.ALL;
		}

		/**
		 * Convert rates to the given time unit.
		 *
		 * @param rateUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertRatesTo(TimeUnit rateUnit) {
			this.rateUnit = rateUnit;
			return this;
		}

		/**
		 * Convert durations to the given time unit.
		 *
		 * @param durationUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertDurationsTo(TimeUnit durationUnit) {
			this.durationUnit = durationUnit;
			return this;
		}

		/**
		 * Only report metrics which match the given filter.
		 *
		 * @param filter a {@link MetricFilter}
		 * @return {@code this}
		 */
		public Builder filter(MetricFilter filter) {
			this.filter = filter;
			return this;
		}

		/**
		 * Builds a {@link ConsoleReporter} with the given properties.
		 *
		 * @return a {@link ConsoleReporter}
		 */
		public InMemoryReporter build() {
			return new InMemoryReporter(registry,
			                            "memory reporter",
			                           filter,
			                           rateUnit,
			                           durationUnit
			                           );
		}
	}
	protected InMemoryReporter( MetricRegistry registry,
	                            String name,
	                            MetricFilter filter,
	                            TimeUnit rateUnit, TimeUnit durationUnit ) {
		super( registry, name, filter, rateUnit, durationUnit );
	}

	@Override
	public void report( SortedMap<String, Gauge> gauges,
	                    SortedMap<String, Counter> counters,
	                    SortedMap<String, Histogram> histograms,
	                    SortedMap<String, Meter> meters,
	                    SortedMap<String, Timer> timers ) {
		throw new NotImplementedException( "WIP" );
	}
}
