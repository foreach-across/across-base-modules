package com.foreach.across.modules.logging.request;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestRequestLoggerJsonProvider {
	@Mock
	private JsonGenerator generator;
	@Mock
	private ILoggingEvent event;
	private RequestLoggerJsonProvider jsonProvider;

	@BeforeEach
	public void setUp() throws Exception {
		reset(generator, event);
		jsonProvider = new RequestLoggerJsonProvider();
	}

	@Test
	public void writeTo() throws IOException {
		when(event.getFormattedMessage()).thenReturn(
				"127.0.0.1\tmethod()\thttp://google.be\t/search?q=abc\tMyController\tmy-method()\tth/spring/view\t200\t123");
		jsonProvider.writeTo(generator, event);
		verify( generator ).writeStringField( "remoteAddress", "127.0.0.1" );
		verify(generator).writeStringField("method", "method()");
		verify(generator).writeStringField("url", "http://google.be");
		verify(generator).writeStringField("servletPath", "/search?q=abc");
		verify(generator).writeStringField("requestMapping", "MyController");
		verify(generator).writeStringField("handlerName", "my-method()");
		verify(generator).writeStringField("viewName", "th/spring/view");
		verify(generator).writeNumberField("status", 200);
		verify(generator).writeNumberField("duration", 123);
	}

	@Test
	public void writeToWithExceptionInGenerator() throws IOException {
		when(event.getFormattedMessage()).thenReturn(
				"127.0.0.1\tmethod()\thttp://google.be\t/search?q=abc\tMyController\tmy-method()\tth/spring/view\t200\t123");
		doThrow(new IOException()).when(generator).writeStringField(anyString(), anyString());
		assertThrows(IOException.class, () -> {
			jsonProvider.writeTo(generator, event);
		});
	}

	@Test
	public void writeToWithExceptionWhileParsing() {
		when(event.getFormattedMessage()).thenReturn(
				"127.0.0.1\tmethod()\thttp://google.be\t/search?q=abc\tMyController\tmy-method()\tth/spring/view\tINVALID-STATUS\t123");
		assertThrows(IOException.class, () -> {
			jsonProvider.writeTo(generator, event);
		});
	}
}