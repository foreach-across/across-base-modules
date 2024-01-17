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

package com.foreach.across.modules.spring.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.ClassUtils;

import javax.servlet.Filter;

/**
 * Proxies a regular {@link WebSecurityConfigurer} but sets the order property explicitly.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
@RequiredArgsConstructor
class HttpSecurityBuilderProxy<T extends HttpSecurityBuilder<T>> implements HttpSecurityBuilder<T>, Ordered
//class HttpSecurityBuilderProxy<T extends SecurityBuilder<SecurityFilterChain>> implements HttpSecurityBuilder<T>, Ordered
//class HttpSecurityBuilderProxy<T extends SecurityBuilder<? extends SecurityFilterChain>> implements HttpSecurityBuilder<T>, Ordered
{
	//private final HttpSecurityBuilder<T> target;
	private final AcrossWebSecurityConfigurer target;
	private final int order;

	@Override
	public int getOrder() {
		return order;
	}

/*
	@Override
	public void init( T builder ) throws Exception {
		target.init( builder );
	}

	@Override
	public void configure( T builder ) throws Exception {
		target.configure( builder );
	}
*/

	@Override
	public String toString() {
		return "HttpSecurityBuilderProxy{" +
				"configurer=" + ClassUtils.getUserClass( target ).getName() +
				", order=" + order +
				'}';
	}

	@Override
	public <C extends SecurityConfigurer<DefaultSecurityFilterChain, T>> C getConfigurer( Class<C> clazz ) {
		return null;
	}

	@Override
	public <C extends SecurityConfigurer<DefaultSecurityFilterChain, T>> C removeConfigurer( Class<C> clazz ) {
		return null;
	}

	@Override
	public <C> void setSharedObject( Class<C> sharedType, C object ) {

	}

	@Override
	public <C> C getSharedObject( Class<C> sharedType ) {
		return null;
	}

	@Override
	public T authenticationProvider( AuthenticationProvider authenticationProvider ) {
		return null;
	}

	@Override
	public T userDetailsService( UserDetailsService userDetailsService ) throws Exception {
		return null;
	}

	@Override
	public T addFilterAfter( Filter filter, Class<? extends Filter> afterFilter ) {
		return null;
	}

	@Override
	public T addFilterBefore( Filter filter, Class<? extends Filter> beforeFilter ) {
		return null;
	}

	@Override
	public T addFilter( Filter filter ) {
		return null;
	}

	@Override
	public DefaultSecurityFilterChain build() throws Exception {
		return null;
	}
}
