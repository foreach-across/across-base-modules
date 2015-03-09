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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.metrics.controllers.MetricsController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricModuleConfiguration
{
	@Bean
	public MetricsController metricsController() {
		return new MetricsController();
	}

	@Bean
	@Exposed
	public AcrossMetricRegistry acrossMetricRegistry() {
		return new AcrossMetricRegistry();
	}
}
