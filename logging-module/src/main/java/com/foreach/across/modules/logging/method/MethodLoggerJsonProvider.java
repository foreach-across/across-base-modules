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

package com.foreach.across.modules.logging.method;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

import java.io.IOException;

/**
 * When logstash is configured this JSON-Provider is used to extend fields we use for our the method logging fields
 * that are being send to logstash
 */
public class MethodLoggerJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent>
{
	@Override
	public void writeTo( JsonGenerator generator, ILoggingEvent event ) throws IOException {
		try {
			String message = event.getFormattedMessage();
			String[] split = message.split( "\t" );
			int methodLevel = Integer.parseInt( split[0] );
			String method = split[1];
			int duration = Integer.parseInt( split[2] );
			JsonWritingUtils.writeNumberField( generator, "methodLevel", methodLevel );
			JsonWritingUtils.writeStringField( generator, "method", method );
			JsonWritingUtils.writeNumberField( generator, "duration", duration );
		}
		catch ( Exception e ) {
			throw new IOException( e );
		}
	}
}
