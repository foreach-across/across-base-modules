package com.foreach.across.modules.logging.method;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMethodLoggerJsonProvider {
	@Mock
	private JsonGenerator generator;
	@Mock
	private ILoggingEvent event;
	private MethodLoggerJsonProvider jsonProvider;

	@BeforeEach
	public void setUp() throws Exception {
		reset(generator, event);
		jsonProvider = new MethodLoggerJsonProvider();
	}

	@Test
	public void writeTo() throws IOException {
		when(event.getFormattedMessage()).thenReturn("1\tmethod()\t123");
		jsonProvider.writeTo(generator, event);
		verify(generator).writeNumberField("methodLevel", 1);
		verify(generator).writeStringField("method", "method()");
		verify(generator).writeNumberField("duration", 123);
	}

	@Test
	public void writeToWithException() throws IOException {
		when(event.getFormattedMessage()).thenReturn("parsing should fail");
		assertThrows(IOException.class, () -> {
			jsonProvider.writeTo(generator, event);
		});
	}

	@Test
	public void writeToWithExceptionInGenerator() throws IOException {
		when(event.getFormattedMessage()).thenReturn("1\tmethod()\t123");
		doThrow(new IOException()).when(generator).writeStringField(anyString(), anyString());
		assertThrows(IOException.class, () -> {
			jsonProvider.writeTo(generator, event);
		});
	}
}