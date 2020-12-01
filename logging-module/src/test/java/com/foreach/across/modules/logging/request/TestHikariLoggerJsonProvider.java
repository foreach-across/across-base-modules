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
public class TestHikariLoggerJsonProvider {
	@Mock
	private JsonGenerator generator;
	@Mock
	private ILoggingEvent event;
	private HikariLoggerJsonProvider jsonProvider;

	@BeforeEach
	public void setUp() throws Exception {
		reset(generator, event);
		jsonProvider = new HikariLoggerJsonProvider();
	}

	@Test
	public void writeTo() throws IOException {
		when(event.getFormattedMessage()).thenReturn("ABC pool stats my-pool (total=1, inUse=2, avail=3, waiting=4)");
		jsonProvider.writeTo(generator, event);
		verify(generator).writeStringField("location", "ABC ");
		verify(generator).writeStringField("pool", "my-pool");
		verify(generator).writeNumberField("total", 1);
		verify(generator).writeNumberField("active", 2);
		verify(generator).writeNumberField("idle", 3);
		verify(generator).writeNumberField("waiting", 4);
	}

	@Test
	public void writeToWithExceptionInGenerator() throws IOException {
		when(event.getFormattedMessage()).thenReturn("ABC pool stats my-pool (total=1, inUse=2, avail=3, waiting=4)");
		doThrow(new IOException()).when(generator).writeStringField(anyString(), anyString());
		assertThrows(IOException.class, () -> {
			jsonProvider.writeTo(generator, event);
		});
	}

}