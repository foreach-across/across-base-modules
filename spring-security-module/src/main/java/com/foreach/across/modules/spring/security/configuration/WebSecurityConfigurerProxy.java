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
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.util.ClassUtils;

import javax.servlet.Filter;

/**
 * Proxies a regular {@link WebSecurityConfigurer} but sets the order property explicitly.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
@RequiredArgsConstructor
class WebSecurityConfigurerProxy<T extends SecurityBuilder<Filter>> implements WebSecurityConfigurer<T>, Ordered
{
	private final WebSecurityConfigurer<T> target;
	private final int order;

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void init( T builder ) throws Exception {
		target.init( builder );
	}

	@Override
	public void configure( T builder ) throws Exception {
		target.configure( builder );
	}

	@Override
	public String toString() {
		return "WebSecurityConfigurerProxy{" +
				"configurer=" + ClassUtils.getUserClass( target ).getName() +
				", order=" + order +
				'}';
	}
}
