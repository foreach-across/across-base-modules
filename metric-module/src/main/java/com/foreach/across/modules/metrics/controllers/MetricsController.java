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
package com.foreach.across.modules.metrics.controllers;

import com.codahale.metrics.*;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.metrics.config.AcrossMetricRegistry;
import com.foreach.across.modules.metrics.config.BaseMetricModuleConfiguration;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.table.Table;
import com.foreach.across.modules.web.table.TableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@DebugWebController
@AcrossDepends(required = DebugWebModule.NAME)
public class MetricsController
{
	@Autowired
	private AcrossMetricRegistry acrossMetricRegistry;

	@Event
	public void buildMenu( DebugMenuEvent event ) {
		List<BaseMetricModuleConfiguration> enabledMetrics = acrossMetricRegistry.getItems();
		if( !CollectionUtils.isEmpty( enabledMetrics ) ) {
			PathBasedMenuBuilder.PathBasedMenuItemBuilder builder = event.builder().group( "/metrics", "Metrics" );
			for ( BaseMetricModuleConfiguration entry : enabledMetrics ) {
				int index = enabledMetrics.indexOf( entry );
				builder.and().item( "/metrics/" + index, entry.getName() );
			}
		}
	}

	@RequestMapping("/metrics/overview")
	public String showMetrics( Model model ) {
		model.addAttribute( "loadedMetricModules", acrossMetricRegistry.getItems() );
		return "th/metrics/metricsList";
	}

	@RequestMapping("/metrics/{index}")
	public String showMetricsJvm( Model model, @PathVariable("index") int index ) {
		TimeUnit rateUnit = TimeUnit.SECONDS;

		List<Table> metrics = new LinkedList<>();
		BaseMetricModuleConfiguration module = acrossMetricRegistry.getItems().get( index );
		MetricRegistry metricRegistry = module.getMetricRegistry();
		Table gauges = new Table( "Gauges" );
		for ( Map.Entry<String, Gauge> entry : metricRegistry.getGauges().entrySet() ) {
			writeField( gauges, entry.getKey(), entry.getValue().getValue() );
		}
		metrics.add( gauges );

		Table counters = new Table( "Counters" );
		for ( Map.Entry<String, Counter> entry : metricRegistry.getCounters().entrySet() ) {
			writeField( counters, entry.getKey(), entry.getValue().getCount() );
		}
		metrics.add( counters );

		Table histograms = new Table( "Histograms" );
		for ( Map.Entry<String, Histogram> entry : metricRegistry.getHistograms().entrySet() ) {
			Histogram histogram = entry.getValue();
			Snapshot snapshot = entry.getValue().getSnapshot();

			writeField( histograms, entry.getKey(), "count", histogram.getCount() );
			writeField( histograms, null, "max", snapshot.getMax() );
			writeField( histograms, null, "mean", snapshot.getMean() );
			writeField( histograms, null, "min", snapshot.getMin() );
			writeField( histograms, null, "p50", snapshot.getMedian() );
			writeField( histograms, null, "p75", snapshot.get75thPercentile() );
			writeField( histograms, null, "p95", snapshot.get95thPercentile() );
			writeField( histograms, null, "p98", snapshot.get98thPercentile() );
			writeField( histograms, null, "p99", snapshot.get99thPercentile() );
			writeField( histograms, null, "p999", snapshot.get999thPercentile() );
			writeField( histograms, null, "stddev", snapshot.getStdDev() );
		}
		metrics.add( histograms );

		Table meters = new Table( "Meters" );
		double rateFactor = rateUnit.toSeconds( 1 );

		for ( Map.Entry<String, Meter> entry : metricRegistry.getMeters().entrySet() ) {
			Meter meter = entry.getValue();
			writeField( meters, entry.getKey(), "count", meter.getCount() );
			writeField( meters, null, "m15_rate", meter.getFifteenMinuteRate() * rateFactor );
			writeField( meters, null, "m1_rate", meter.getOneMinuteRate() * rateFactor );
			writeField( meters, null, "m5_rate", meter.getFiveMinuteRate() * rateFactor );
			writeField( meters, null, "mean_rate", meter.getMeanRate() * rateFactor );
			writeField( meters, null, "units", calculateRateUnit( rateUnit, "events" ) );
		}
		metrics.add( meters );

		Table timers = new Table( "Timers" );

		TimeUnit durationUnit = TimeUnit.SECONDS;
		double durationFactor = 1.0 / durationUnit.toNanos( 1 );
		for ( Map.Entry<String, Timer> entry : metricRegistry.getTimers().entrySet() ) {
			Timer timer = entry.getValue();
			Snapshot snapshot = timer.getSnapshot();

			writeField( timers, entry.getKey(), "count", timer.getCount() );
			writeField( timers, null, "max", snapshot.getMax() * durationFactor );
			writeField( timers, null, "mean", snapshot.getMean() * durationFactor );
			writeField( timers, null, "min", snapshot.getMin() * durationFactor );
			writeField( timers, null, "p50", snapshot.getMedian() * durationFactor );
			writeField( timers, null, "p75", snapshot.get75thPercentile() * durationFactor );
			writeField( timers, null, "p95", snapshot.get95thPercentile() * durationFactor );
			writeField( timers, null, "p98", snapshot.get98thPercentile() * durationFactor );
			writeField( timers, null, "p99", snapshot.get99thPercentile() * durationFactor );
			writeField( timers, null, "p999", snapshot.get999thPercentile() * durationFactor );
			writeField( timers, null, "stddev", snapshot.getStdDev() * durationFactor );
			writeField( timers, null, "m15_rate", timer.getFifteenMinuteRate() * rateFactor );
			writeField( timers, null, "m1_rate", timer.getOneMinuteRate() * rateFactor );
			writeField( timers, null, "m5_rate", timer.getFiveMinuteRate() * rateFactor );
			writeField( timers, null, "mean_rate", timer.getMeanRate() * rateFactor );
			writeField( timers, null, "duration_units", durationUnit );
			writeField( timers, null, "rate_units", rateUnit );
		}
		metrics.add( timers );

		model.addAttribute( "metrics", metrics );
		return "th/metrics/metricsDetail";
	}

	private void writeField( Table table, String name, Object number ) {
		TableRow row = new TableRow( name, number );
		table.addRow( row );
	}

	private void writeField( Table table, String prefix, String name, Object number ) {
		TableRow row = new TableRow( prefix, name, number );
		table.addRow( row );
	}

	private static String calculateRateUnit( TimeUnit unit, String name ) {
		final String s = unit.toString().toLowerCase( Locale.US );
		return name + '/' + s.substring( 0, s.length() - 1 );
	}
}