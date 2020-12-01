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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * When logstash is configured this JSON-Provider is used to extend fields we use for our the request logging fields
 * that are being sent to logstash
 */
public class RequestLoggerJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent>
{
	@Override
	public void writeTo( JsonGenerator generator, ILoggingEvent event ) throws IOException {
		try {
			String[] split = StringUtils.split(event.getFormattedMessage(), '\t');
			JsonWritingUtils.writeStringField(generator, "remoteAddress", split[0]);
			JsonWritingUtils.writeStringField(generator, "method", split[1]);
			JsonWritingUtils.writeStringField(generator, "url", split[2]);
			JsonWritingUtils.writeStringField(generator, "servletPath", split[3]);
			JsonWritingUtils.writeStringField(generator, "requestMapping", split[4]);
			JsonWritingUtils.writeStringField(generator, "handlerName", split[5]);
			JsonWritingUtils.writeStringField(generator, "viewName", split[6]);
			JsonWritingUtils.writeNumberField(generator, "status", Integer.parseInt(split[7]));
			JsonWritingUtils.writeNumberField(generator, "duration", Integer.parseInt(split[8]));
		}
		catch ( Exception e ) {
			throw new IOException( e );
		}
	}
}
