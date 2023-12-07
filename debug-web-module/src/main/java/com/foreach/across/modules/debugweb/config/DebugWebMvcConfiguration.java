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
package com.foreach.across.modules.debugweb.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.debugweb.DebugWebModuleSettings;
import com.foreach.across.modules.debugweb.mvc.DebugMenu;
import com.foreach.across.modules.debugweb.mvc.DebugMenuBuilder;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfiguration;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Declares a separate handler for debug mappings.
 */
@Configuration
public class DebugWebMvcConfiguration extends PrefixingHandlerMappingConfiguration
{
	@Autowired
	private MenuFactory menuFactory;

	@Autowired
	private DebugWebModuleSettings debugWebModuleSettings;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void initialize() {
		menuFactory.addMenuBuilder( debugMenuBuilder(), DebugMenu.class );
	}

	@Override
	protected String getPrefixPath() {
		return debugWebModuleSettings.getRootPath();
	}

	@Override
	protected ClassFilter getHandlerMatcher() {
		return new AnnotationClassFilter( DebugWebController.class, true );
	}

	@Bean(name = "debugWebHandlerMapping")
	@Exposed
	@Override
	public PrefixingRequestMappingHandlerMapping controllerHandlerMapping() {
		return super.controllerHandlerMapping();
	}

	@Bean
	public DebugMenuBuilder debugMenuBuilder() {
		return new DebugMenuBuilder();
	}
}

