package com.foreach.across.modules.logging.installers;

import com.foreach.across.modules.logging.LoggingModuleSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class TestIndexPatternsInstaller
{

	public static final String KIBANA_SERVER = "http://kibana.com";
	public static final String APPLICATION_NAME = "my-app";
	@Mock
	private Environment env;
	private IndexPatternsInstaller installer;
	private MockRestServiceServer mockServer;

	@Before
	public void setUp() throws Exception {
		when( env.getProperty( LoggingModuleSettings.KIBANA_CONFIGURATION_SERVER ) ).thenReturn( KIBANA_SERVER );
		when( env.getProperty( LoggingModuleSettings.LOGSTASH_CONFIGURATION_APPLICATION ) ).thenReturn( APPLICATION_NAME );
		when( env.getActiveProfiles() ).thenReturn( new String[] { "acc" } );
		installer = new IndexPatternsInstaller( env );
		RestTemplate restTemplate = new RestTemplate();
		mockServer = MockRestServiceServer.createServer( restTemplate );
		setField( installer, "restTemplate", restTemplate );
	}

	@Test
	public void installIndexPatterns() {
		String request = String.format( "{ \"attributes\": {\"title\": \"%s\", \"timeFieldName\": \"@timestamp\"} }",
		                                String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "errors" ) );
		mockServer.expect(
				requestTo( KIBANA_SERVER + "/api/saved_objects/index-pattern/" + String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "errors" ) ) )
		          .andExpect( method( HttpMethod.POST ) )
		          .andExpect( content().string( request ) )
		          .andRespond( withSuccess() );
		request = String.format( "{ \"attributes\": {\"title\": \"%s\", \"timeFieldName\": \"@timestamp\"} }",
		                         String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "methods" ) );
		mockServer.expect(
				requestTo( KIBANA_SERVER + "/api/saved_objects/index-pattern/" + String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "methods" ) ) )
		          .andExpect( method( HttpMethod.POST ) )
		          .andExpect( content().string( request ) )
		          .andRespond( withSuccess() );
		request = String.format( "{ \"attributes\": {\"title\": \"%s\", \"timeFieldName\": \"@timestamp\"} }",
		                         String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "hikari" ) );
		mockServer.expect(
				requestTo( KIBANA_SERVER + "/api/saved_objects/index-pattern/" + String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "hikari" ) ) )
		          .andExpect( method( HttpMethod.POST ) )
		          .andExpect( content().string( request ) )
		          .andRespond( withSuccess() );
		request = String.format( "{ \"attributes\": {\"title\": \"%s\", \"timeFieldName\": \"@timestamp\"} }",
		                         String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "requests" ) );
		mockServer.expect(
				requestTo( KIBANA_SERVER + "/api/saved_objects/index-pattern/" + String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "requests" ) ) )
		          .andExpect( method( HttpMethod.POST ) )
		          .andExpect( content().string( request ) )
		          .andRespond( withSuccess() );
		request = String.format( "{ \"value\": \"%s\"  }", String.format( "%s-logs-%s-%s*", "acc", APPLICATION_NAME, "requests" ) );
		mockServer.expect( requestTo( KIBANA_SERVER + "/api/kibana/settings/defaultIndex" ) )
		          .andExpect( method( HttpMethod.POST ) )
		          .andExpect( content().string( request ) )
		          .andRespond( withSuccess() );
		installer.installIndexPatterns();
	}
}
