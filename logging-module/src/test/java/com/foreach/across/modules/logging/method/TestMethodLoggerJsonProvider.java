package com.foreach.across.modules.logging.method;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestMethodLoggerJsonProvider
{
	@Mock
	private JsonGenerator generator;
	@Mock
	private ILoggingEvent event;
	private MethodLoggerJsonProvider jsonProvider;

	@Before
	public void setUp() throws Exception {
		reset( generator, event );
		jsonProvider = new MethodLoggerJsonProvider();
	}

	@Test
	public void writeTo() throws IOException {
		when( event.getFormattedMessage() ).thenReturn( "1\tmethod()\t123" );
		jsonProvider.writeTo( generator, event );
		verify( generator ).writeNumberField( "methodLevel", 1 );
		verify( generator ).writeStringField( "method", "method()" );
		verify( generator ).writeNumberField( "duration", 123 );
	}

	@Test(expected = IOException.class)
	public void writeToWithException() throws IOException {
		when( event.getFormattedMessage() ).thenReturn( "parsing should fail" );
		jsonProvider.writeTo( generator, event );
	}

	@Test(expected = IOException.class)
	public void writeToWithExceptionInGenerator() throws IOException {
		when( event.getFormattedMessage() ).thenReturn( "1\tmethod()\t123" );
		doThrow( new IOException() ).when( generator ).writeStringField( anyString(), anyString() );
		jsonProvider.writeTo( generator, event );
	}
}