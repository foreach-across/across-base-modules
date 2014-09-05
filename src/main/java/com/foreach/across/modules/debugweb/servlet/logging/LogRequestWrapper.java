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
