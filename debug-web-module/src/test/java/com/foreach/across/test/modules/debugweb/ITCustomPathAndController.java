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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITCustomPathAndController.Config.class)
@TestPropertySource(
		properties = {
				"debugWebModule.security.enabled=false",
				"debugWebModule.root-path=/test/debug",
				"debugWebModule.dashboard=/somedashboard"
		}
)
public class ITCustomPathAndController
{
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void initMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
	}

	@Test
	public void customDashboardShouldBeRetrieved() throws Exception {
		mockMvc.perform( get( "/test/debug" ) )
		       .andExpect( status().is3xxRedirection() )
		       .andExpect( header().string( "location", "/test/debug/somedashboard" ) );
	}

	@Test
	public void defaultPathShouldNotWork() throws Exception {
		mockMvc.perform( get( "/debug" ) )
		       .andExpect( status().isNotFound() );
	}

	@Configuration
	@EnableAcrossContext(DebugWebModule.NAME)
	protected static class Config
	{
	}
}
