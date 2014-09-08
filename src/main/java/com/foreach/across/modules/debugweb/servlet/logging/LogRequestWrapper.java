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
package com.foreach.across.modules.debugweb.servlet.logging;

import org.apache.commons.io.input.TeeInputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class LogRequestWrapper extends HttpServletRequestWrapper
{
	private final FixedByteArrayOutputStream bos = new FixedByteArrayOutputStream( 100 * 1024 );

	public LogRequestWrapper( HttpServletRequest req ) {
		super( req );
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new ServletInputStream()
		{
			private TeeInputStream tee = new TeeInputStream( LogRequestWrapper.super.getInputStream(), bos );

			@Override
			public int read() throws IOException {
				return tee.read();
			}
		};
	}

	public long payloadSize() {
		return bos.getRealSize();
	}

	public boolean isMaximumReached() {
		return bos.isMaximumReached();
	}

	public byte[] toByteArray() {
		return bos.toByteArray();
	}
}
