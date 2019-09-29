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

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.ConditionalOnDevelopmentMode;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.Filter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@DebugWebController
@ConditionalOnAcrossModule(SpringSecurityModule.NAME)
@RequiredArgsConstructor
public class SecurityFiltersController
{
	private final AcrossContextBeanRegistry beanRegistry;

	@EventListener
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .group( "/across/security", "Security" ).and()
		     .item( "/across/security/filters", "Security filters", "/security/filters" );
	}

	@RequestMapping("/security/filters")
	public String showFilters( Model model ) {
		FilterChainProxy securityFilterChainProxy = beanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME,
		                                                                                  FilterChainProxy.class );
		model.addAttribute(
				"securityFilters",
				securityFilterChainProxy.getFilterChains().stream()
				                        .map( this::toSecurityFilterInfo )
				                        .collect( Collectors.toList() )
		);

		return DebugWeb.VIEW_SECURITY_FILTERS;
	}

	private SecurityFilterInfo toSecurityFilterInfo( SecurityFilterChain filterChain ) {
		if ( filterChain instanceof DefaultSecurityFilterChain ) {
			return new SecurityFilterInfo( ( (DefaultSecurityFilterChain) filterChain ).getRequestMatcher(),
			                               filterChain.getFilters() );
		}
		return new SecurityFilterInfo( null, filterChain.getFilters() );
	}

	@Getter
	@RequiredArgsConstructor
	class SecurityFilterInfo
	{
		private final RequestMatcher requestMatcher;
		private final List<Filter> filters;
	}
}
