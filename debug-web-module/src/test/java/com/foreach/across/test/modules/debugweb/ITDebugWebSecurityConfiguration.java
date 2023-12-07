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

package com.foreach.across.test.modules.debugweb;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Arne Vandamme
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext
@ContextConfiguration(classes = ITDebugWebSecurityConfiguration.Config.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ITDebugWebSecurityConfiguration.Config.class,
	properties = {
			"debugWebModule.security.username=foo",
			"debugWebModule.security.password=bar",
	        "debugWebModule.security.ip-addresses="
	})
public class ITDebugWebSecurityConfiguration
{
	private static final String DEBUG_URL = "/debug";

	private final TestRestTemplate restTemplate = new TestRestTemplate();

	@Value("${local.server.port}")
	private int port;

	@Test
	public void debugPathShouldBeProtected() throws Exception {
		ResponseEntity<String> response = restTemplate.exchange( url( DEBUG_URL ), HttpMethod.GET, null, String.class, Collections.emptyMap() );
		assertNotNull( response );
		assertEquals( HttpStatus.UNAUTHORIZED, response.getStatusCode() );
	}

	private String url( String relativePath ) {
		return "http://localhost:" + port + relativePath;
	}

	@AcrossApplication(modules =  { DebugWebModule.NAME, SpringSecurityModule.NAME })
	protected static class Config
	{
	}
}
