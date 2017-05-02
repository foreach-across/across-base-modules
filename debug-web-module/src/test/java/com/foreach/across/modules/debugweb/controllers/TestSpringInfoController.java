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

package com.foreach.across.modules.debugweb.controllers;

import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author: Sander Van Loock
 * @since: 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestSpringInfoController
{
	@Autowired
	private SpringInfoController controller;

	@Test
	public void showInterceptors() throws Exception {
		Model model = mock( Model.class );
		String actualView = controller.showInterceptors( model );

		assertEquals( DebugWeb.VIEW_SPRING_INTERCEPTORS, actualView );
		ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass( List.class );
		verify( model, times( 1 ) ).addAttribute( anyString(), listArgumentCaptor.capture() );

		assertEquals( 1, listArgumentCaptor.getValue().size() );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public SpringInfoController springInfoController() {
			return new SpringInfoController();
		}

		@Bean(name = "testHandlerMapping")
		public PrefixingRequestMappingHandlerMapping controllerHandlerMapping() {
			PrefixingRequestMappingHandlerMapping handlerMapping =
					new PrefixingRequestMappingHandlerMapping( "/", new AnnotationClassFilter( DebugWebController.class,
					                                                                           true ) );
			handlerMapping.setOrder( 0 );

			return handlerMapping;
		}

		@Bean
		public CustomInterceptor customInterceptor() {
			return new CustomInterceptor();
		}
	}
}