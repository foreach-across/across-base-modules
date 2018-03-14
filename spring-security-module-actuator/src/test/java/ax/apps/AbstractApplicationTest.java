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

package ax.apps;

import lombok.val;
import org.apache.tomcat.util.codec.binary.Base64;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.StringAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ActuatorApplication.class)
abstract class AbstractApplicationTest
{
	@Value("${local.server.port}")
	private int port;

	private RestTemplate restTemplate;

	@Before
	public void before() {
		restTemplate = new RestTemplateBuilder()
				.rootUri( "http://localhost:" + port )
				.build();
	}

	@Test
	public void homePageIsOpen() {
		assertResponseWithStatus( "/", HttpStatus.OK ).isEqualTo( "hello" );
	}

	@Test
	public void errorPageIsOpen() {
		assertResponseWithStatus( "/error", HttpStatus.INTERNAL_SERVER_ERROR );
	}

	protected AbstractCharSequenceAssert<?, String> assertResponseWithStatus( String url, HttpStatus status ) {
		try {
			val response = restTemplate.getForEntity( url, String.class );
			assertThat( response.getStatusCode() ).isEqualTo( status );
			return assertThat( response.getBody() );
		}
		catch ( HttpStatusCodeException sce ) {
			assertThat( sce.getStatusCode() ).isEqualTo( status );
			return new StringAssert( null );
		}
	}

	protected AbstractCharSequenceAssert<?, String> assertAuthenticatedResponseWithStatus( String url, HttpStatus status ) {
		val authHeaders = new HttpHeaders()
		{{
			String auth = "john.doe:somepwd";
			byte[] encodedAuth = Base64.encodeBase64(
					auth.getBytes( Charset.forName( "US-ASCII" ) ) );
			String authHeader = "Basic " + new String( encodedAuth );
			set( "Authorization", authHeader );
		}};
		val response = restTemplate.exchange( url, HttpMethod.GET, new HttpEntity<>( authHeaders ), String.class );
		assertThat( response.getStatusCode() ).isEqualTo( status );
		return assertThat( response.getBody() );
	}
}
