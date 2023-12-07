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

import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.modules.debugweb.DebugWebModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * @author Arne Vandamme
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITCustomMaskedProperties.Config.class)
@TestPropertySource(
		properties = {
				"BASH_FUNCTION=() {  local root=\"${1}\"; }",
				"my.password.value=passwordvalue",
				"some.secret=secretvalue",
				"custom.property=custom.value",
				"debugWebModule.properties.masks=^custom.*",
				"debugWebModule.properties.masked-properties=some.secret",
				"debugWebModule.security.enabled=false"
		}
)
public class ITCustomMaskedProperties
{
	private static final String PROPERTIES_URL = "/debug/across/browser/properties/2";

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	public void initMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
	}

	@Test
	public void unknownPlaceHoldersShouldNotBeResolved() throws Exception {
		mockMvc.perform( get( PROPERTIES_URL ) )
		       .andExpect( content().string( containsString( "() {  local root=&quot;${1}&quot;; }" ) ) );
	}

	@Test
	public void passwordPropertyShouldBeVisible() throws Exception {
		mockMvc.perform( get( PROPERTIES_URL ) )
		       .andExpect( content().string( containsString( "my.password.value" ) ) )
		       .andExpect( content().string( containsString( "passwordvalue" ) ) );
	}

	@Test
	public void secretPropertyShouldBeHidden() throws Exception {
		mockMvc.perform( get( PROPERTIES_URL ) )
		       .andExpect( content().string( containsString( "some.secret" ) ) )
		       .andExpect( content().string( not( containsString( "secretvalue" ) ) ) );
	}

	@Test
	public void customPropertyShouldBeHidden() throws Exception {
		mockMvc.perform( get( PROPERTIES_URL ) )
		       .andExpect( content().string( containsString( "custom.property" ) ) )
		       .andExpect( content().string( not( containsString( "custom.value" ) ) ) );
	}

	@Configuration
	@EnableAcrossContext(DebugWebModule.NAME)
	protected static class Config
	{
	}
}
