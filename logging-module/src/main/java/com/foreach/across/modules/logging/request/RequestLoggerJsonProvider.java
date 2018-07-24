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

public class RequestLoggerJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent>
{
	@Override
	public void writeTo( JsonGenerator generator, ILoggingEvent event ) throws IOException {
		try {
			String message = event.getFormattedMessage();
			String[] split = message.split( "\t" );
			String remoteAddress = split[0];
			String method = split[1];
			String url = split[2];
			String servletPath = split[3];
			String requestMapping = split[4];
			String handlerName = split[5];
			String viewName = split[6];
			int status = Integer.parseInt( split[7] );
			int duration = Integer.parseInt( split[8] );
			JsonWritingUtils.writeStringField( generator, "remoteAddress", remoteAddress );
			JsonWritingUtils.writeStringField( generator, "method", method );
			JsonWritingUtils.writeStringField( generator, "url", url );
			JsonWritingUtils.writeStringField( generator, "servletPath", servletPath );
			JsonWritingUtils.writeStringField( generator, "requestMapping", requestMapping );
			JsonWritingUtils.writeStringField( generator, "handlerName", handlerName );
			JsonWritingUtils.writeStringField( generator, "viewName", viewName );
			JsonWritingUtils.writeNumberField( generator, "status", status );
			JsonWritingUtils.writeNumberField( generator, "duration", duration );
		}
		catch ( Exception e ) {
			throw new IOException( e );
		}
	}
}
