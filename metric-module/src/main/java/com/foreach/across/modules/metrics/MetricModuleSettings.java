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

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;

/**
 * @author Marc Vanbrabant
 */
public class MetricModuleSettings extends AcrossModuleSettings
{
	public static final String METRICS_INMEMORYDATASTORE_ENABLED = "metricsModule.inMemoryDataStoreEnabled";
	public static final String METRICS_JVM_ENABLED = "metricsModule.jvmEnabled";
	public static final String METRICS_DATASOURCE_ENABLED = "metricsModule.DataSourceEnabled";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( METRICS_INMEMORYDATASTORE_ENABLED, Boolean.class, false, "Are we storing metrics in memory for historic purposes" );
		registry.register( METRICS_JVM_ENABLED, Boolean.class, true, "Should we track JVM metrics" );
		registry.register( METRICS_DATASOURCE_ENABLED, Boolean.class, true, "Should we track DataSource metrics (with HikariDataSource)" );
	}

	public boolean isMetricsJvmEnabled() {
		return getProperty( METRICS_JVM_ENABLED, Boolean.class );
	}

	public boolean isMetricsDataSourceEnabled() {
		return getProperty( METRICS_DATASOURCE_ENABLED, Boolean.class );
	}

	public boolean isInMemoryDataStoreEnabled() {
		return getProperty( METRICS_INMEMORYDATASTORE_ENABLED, Boolean.class );
	}
}
