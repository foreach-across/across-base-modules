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

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;

public enum AcrossMetric
{
	JVM( "com.codahale.metrics.jvm.MemoryUsageGaugeSet", "<groupId>io.dropwizard.metrics</groupId>, <artifactId>metrics-jvm</artifactId>" ),
	DATASOURCES();
//	SERVLETS(),
//	EHCACHE(),
//	LOGBACK();


	private MetricRegistry metricRegistry;
	private String dependencyClass;
	private String information;

	AcrossMetric() {
		this( null, null );
	}

	AcrossMetric( String dependencyClass, String information ) {
		this.metricRegistry = new MetricRegistry();
		this.dependencyClass = dependencyClass;
		this.information = information;
	}

	public void validateDependency() {
		if( dependencyClass != null ) {
			try {
				Class.forName( dependencyClass );
			}
			catch ( ClassNotFoundException e ) {
				String implementationVersion = Metric.class.getPackage().getImplementationVersion();
				throw new IllegalArgumentException( this.name() + " Metrics depends on " + information + ", please add the dependency or disable the metric in MetricModuleSettings version: " + implementationVersion );
			}
		}
	}

	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
}
