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

import com.foreach.across.core.annotations.AcrossCondition;
import org.springframework.context.annotation.Configuration;

@Configuration
@AcrossCondition("settings.inMemoryDataStoreEnabled")
public class InMemoryDataStoreConfiguration
{
//	@Autowired
//	private AcrossMetricRegistry acrossMetricRegistry;

//	@Bean
//	public InMemoryDataStore inMemoryDataStore() {
//		for( Map.Entry<AcrossMetric, BaseModuleConfiguration> item : acrossMetricRegistry.getItems().entrySet() ) {
//			MetricRegistry metricRegistry = item.getKey().getMetricRegistry();
//			InMemoryReporter memoryReporter = InMemoryReporter.forRegistry( metricRegistry ).convertDurationsTo( TimeUnit.SECONDS ).convertRatesTo( TimeUnit.SECONDS ).build();
//			memoryReporter.start( 10, TimeUnit.SECONDS );
//		}
//		return new InMemoryDataStore();
//	}
}
