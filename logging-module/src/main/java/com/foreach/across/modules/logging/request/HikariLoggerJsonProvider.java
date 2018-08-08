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

package com.foreach.across.modules.logging.request;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * When logstash is configured this JSON-Provider is used to extend fields we use for our the hikari logging fields
 * that are being send to logstash
 */
public class HikariLoggerJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent>
{
	private Pattern pattern = Pattern.compile( "(.*)pool stats (.*) \\(total=(\\d+), inUse=(\\d+), avail=(\\d+), waiting=(\\d+)\\)" );

	@Override
	public void writeTo( JsonGenerator generator, ILoggingEvent event ) throws IOException {
		String message = event.getFormattedMessage();
		Matcher matcher = pattern.matcher( message );
		if(matcher.find()){
			String hikariMessage = matcher.group( 1);
			String pool = matcher.group( 2 );
			int total = Integer.parseInt( matcher.group( 3 ) );
			int active = Integer.parseInt( matcher.group( 4 ) );
			int idle = Integer.parseInt( matcher.group( 5 ) );
			int waiting = Integer.parseInt( matcher.group( 6 ) );
			JsonWritingUtils.writeStringField( generator, "location", hikariMessage );
			JsonWritingUtils.writeStringField( generator, "pool", pool );
			JsonWritingUtils.writeNumberField( generator, "total", total );
			JsonWritingUtils.writeNumberField( generator, "active", active );
			JsonWritingUtils.writeNumberField( generator, "idle", idle );
			JsonWritingUtils.writeNumberField( generator, "waiting", waiting );
		}
	}
}
