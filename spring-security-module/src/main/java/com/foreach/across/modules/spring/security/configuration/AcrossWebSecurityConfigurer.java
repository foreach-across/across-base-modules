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

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * Interface to be implemented in modules that want to use the default SpringSecurityModule configuration
 * abilities. Beans of this type can be declared inside a module and will be turned into actual security
 * configurers by SpringSecurityModule.
 * <p/>
 * This interface provides only the more common configuration methods, more advanced security configuration
 * should be done by injecting a {@link org.springframework.security.config.annotation.web.WebSecurityConfigurer}
 * directly in the SpringSecurityModule.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
public interface AcrossWebSecurityConfigurer
{
	/**
	 * Overriding this has the same effect as {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#WebSecurityConfigurerAdapter(boolean)}
	 */
	default boolean disableDefaults() {
		return false;
	}

	default void configure( AuthenticationManagerBuilder auth ) throws Exception {
	}

	default void configure( WebSecurity web ) throws Exception {
	}

	default DefaultSecurityFilterChain configure( HttpSecurity http ) throws Exception {
		return null;
	}
}
