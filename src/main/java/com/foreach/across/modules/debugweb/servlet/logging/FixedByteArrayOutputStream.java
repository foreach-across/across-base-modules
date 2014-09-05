package com.foreach.across.modules.debugweb.servlet.logging;

import java.io.ByteArrayOutputStream;

public class FixedByteArrayOutputStream extends ByteArrayOutputStream
{
	private int maxSize;
	private boolean maximumReached;
	private long byteCount;

	public FixedByteArrayOutputStream( int maxSize ) {
		super();
		this.maxSize = maxSize;
	}

	public FixedByteArrayOutputStream( int initialSize, int maxSize ) {
		super( initialSize );
		this.maxSize = maxSize;
	}

	@Override
	public synchronized void write( byte[] b, int off, int len ) {
		if( maximumNotReached() ) {
			super.write( b, off, len );
		} else {
			maximumReached = true;
		}
		byteCount = byteCount + len;
	}

	@Override
	public synchronized void write( int b ) {
		if( maximumNotReached() ) {
			super.write( b );
		} else {
			maximumReached = true;
		}
		byteCount++;
	}

	private boolean maximumNotReached() {
		return !maximumReached && byteCount < maxSize;
	}

	public long getRealSize() {
		return byteCount;
	}

	public boolean isMaximumReached() {
		return maximumReached;
	}
}
