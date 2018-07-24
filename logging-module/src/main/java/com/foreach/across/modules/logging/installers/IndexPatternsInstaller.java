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

package com.foreach.across.modules.logging.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Installer(
		description = "Create index patterns for application on Kibana",
		version = 1,
		phase = InstallerPhase.AfterModuleBootstrap
)
@Slf4j
@ConditionalOnProperty(name = LoggingModuleSettings.KIBANA_CONFIGURATION_SERVER)
@RequiredArgsConstructor
public class IndexPatternsInstaller
{
	private final Environment env;
	private RestTemplate restTemplate;

	@PostConstruct
	public void setup() {
		int timeout = 5000;
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout( timeout );
		requestFactory.setConnectTimeout( timeout );
		this.restTemplate = new RestTemplate( requestFactory );
	}

	@InstallerMethod
	public void installIndexPatterns() {
		String application = env.getProperty( LoggingModuleSettings.LOGSTASH_CONFIGURATION_APPLICATION );

		if ( env.getActiveProfiles().length > 0 ) {
			Arrays.stream( env.getActiveProfiles() ).forEach( profile -> {
				createIndexPattern( getPattern( profile, application, "errors" ) );
				createIndexPattern( getPattern( profile, application, "methods" ) );
				createIndexPattern( getPattern( profile, application, "hikari" ) );
				createIndexPattern( getPattern( profile, application, "requests" ) );
			} );

			setDefaultPattern( getPattern( env.getActiveProfiles()[0], application, "requests" ) );
		}
	}

	private void setDefaultPattern( String pattern ) {
		URI kibanaEndpoint = UriComponentsBuilder
				.fromHttpUrl( env.getProperty( LoggingModuleSettings.KIBANA_CONFIGURATION_SERVER ) )
				.path( "/api/kibana/settings/defaultIndex" )
				.build()
				.toUri();

		LOG.debug( "Setting index-pattern as default: {} at {}", pattern, kibanaEndpoint );

		String request = String.format( "{ \"value\": \"%s\"  }", pattern );

		try {
			if ( restTemplate.postForEntity( kibanaEndpoint, createRequest( request ), Object.class ).getStatusCode().is2xxSuccessful() ) {
				LOG.debug( "Successfully set index-pattern {} as default", pattern );
			}
		}
		catch ( RestClientException e ) {
			LOG.error( "Error setting {} as default", pattern, e );
		}
	}

	private void createIndexPattern( String pattern ) {
		URI kibanaEndpoint = UriComponentsBuilder
				.fromHttpUrl( env.getProperty( LoggingModuleSettings.KIBANA_CONFIGURATION_SERVER ) )
				.path( "/api/saved_objects/index-pattern/{pattern}" )
				.buildAndExpand( pattern )
				.toUri();

		LOG.debug( "Creating index-pattern: {} at {}", pattern, kibanaEndpoint );

		String request = String.format( "{ \"attributes\": {\"title\": \"%s\", \"timeFieldName\": \"@timestamp\"} }", pattern );

		try {
			if ( restTemplate.postForEntity( kibanaEndpoint, createRequest( request ), Object.class ).getStatusCode().is2xxSuccessful() ) {
				LOG.debug( "Successfully created index-pattern {}", pattern );
			}
		}
		catch ( HttpClientErrorException e ) {
			if ( Objects.equals( HttpStatus.CONFLICT, e.getStatusCode() ) ) {
				LOG.debug( "Pattern {} already exists. Skipping.", pattern );
			}
		}
		catch ( RestClientException e ) {
			LOG.error( "Error creating pattern {}", pattern, e );
		}
	}

	private String getPattern( String profile, String application, String appender ) {
		return String.format( "%s-logs-%s-%s*", profile, application, appender );
	}

	private HttpEntity<String> createRequest( String request ) {
		HttpHeaders headers = new HttpHeaders();
		headers.add( "kbn-xsrf", UUID.randomUUID().toString() );

		return new HttpEntity<>( request, headers );
	}
}
