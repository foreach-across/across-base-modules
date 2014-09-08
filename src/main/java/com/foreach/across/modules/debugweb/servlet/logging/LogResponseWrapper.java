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

import org.apache.commons.io.output.TeeOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class LogResponseWrapper extends HttpServletResponseWrapper
{
	private FixedByteArrayOutputStream bos = new FixedByteArrayOutputStream( 100 * 1024 );
	private PrintWriter writer = new PrintWriter( bos );

	public LogResponseWrapper( HttpServletResponse response ) {
		super( response );
	}

	public String getContent() {
		return bos.toString();
	}

	@Override
	public ServletResponse getResponse() {
		return this;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new TeePrintWriter( super.getWriter(), writer );
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream()
		{
			private TeeOutputStream tee = new TeeOutputStream( LogResponseWrapper.super.getOutputStream(), bos );

			@Override
			public void write( int b ) throws IOException {
				tee.write( b );
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

	private static class TeePrintWriter extends PrintWriter
	{
		private PrintWriter branch;

		public TeePrintWriter( PrintWriter main, PrintWriter branch ) {
			super( main, true );
			this.branch = branch;
		}

		public void write( char buf[], int off, int len ) {
			super.write( buf, off, len );
			super.flush();
			branch.write( buf, off, len );
			branch.flush();
		}

		public void write( String s, int off, int len ) {
			super.write( s, off, len );
			super.flush();
			branch.write( s, off, len );
			branch.flush();
		}

		public void write( int c ) {
			super.write( c );
			super.flush();
			branch.write( c );
			branch.flush();
		}

		public void flush() {
			super.flush();
			branch.flush();
		}

		@Override
		public void close() {
			super.close();
			branch.close();
		}
	}
}
