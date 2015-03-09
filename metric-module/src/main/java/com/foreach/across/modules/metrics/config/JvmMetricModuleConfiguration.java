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
package com.foreach.across.modules.metrics.config;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.foreach.across.core.annotations.AcrossCondition;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.Map;

@Configuration
@AcrossCondition("settings.metricsJvmEnabled")
public class JvmMetricModuleConfiguration extends BaseMetricModuleConfiguration
{
	public JvmMetricModuleConfiguration() {
		super( new MetricRegistry() );
	}

	@PostConstruct
	public void setup() {
		MetricRegistry registry = getMetricRegistry();

		registerAll( "gc", new GarbageCollectorMetricSet(), registry );
		registerAll( "buffers", new BufferPoolMetricSet( ManagementFactory.getPlatformMBeanServer() ), registry );
		registerAll( "memory", new MemoryUsageGaugeSet(), registry );
		registerAll( "threads", new ThreadStatesGaugeSet(), registry );
	}

	private void registerAll( String prefix, MetricSet metricSet, MetricRegistry registry ) {
		for ( Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet() ) {
			if ( entry.getValue() instanceof MetricSet ) {
				registerAll( prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry );
			}
			else {
				registry.register( prefix + "." + entry.getKey(), entry.getValue() );
			}
		}
	}

	@Override
	public String getDependencyClass() {
		return "com.codahale.metrics.jvm.MemoryUsageGaugeSet";
	}

	@Override
	public String getDependencyInfo() {
		return "<groupId>io.dropwizard.metrics</groupId>, <artifactId>metrics-jvm</artifactId>";
	}

	@Override
	public String getName() {
		return "JVM";
	}
}
