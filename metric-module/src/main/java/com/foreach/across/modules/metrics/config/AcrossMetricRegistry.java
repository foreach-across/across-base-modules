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

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AcrossMetricRegistry
{
	private List<BaseMetricModuleConfiguration> registry = new LinkedList<>();

	public void register( BaseMetricModuleConfiguration baseModuleConfiguration ) {
		registry.add( baseModuleConfiguration );
	}

	public List<BaseMetricModuleConfiguration> getItems() {
		return Collections.unmodifiableList( registry );
	}

	public BaseMetricModuleConfiguration getAcrossMetric( String metricName ) {
		for( BaseMetricModuleConfiguration module : registry ) {
			if( StringUtils.equals( module.getName(), metricName ) ) {
				return module;
			}
		}
		return null;
	}
}
