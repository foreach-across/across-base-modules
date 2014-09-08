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

/**
 * Interface to be implemented in modules that want to use the default SpringSecurityModule configuration
 * abilities.
 *
 * @author Arne Vandamme
 * @since 1.0.3
 */
public interface SpringSecurityWebConfigurer
{
	/**
	 * Property to indicate the default security configuration should be disabled.  This is considered
	 * advanced use as it requires more knowledge of the Spring security inner workings.
	 *
	 * @return True if the default security configuration should be disabled for this configurer.
	 */
	boolean isDisableDefaults();

	void configure( AuthenticationManagerBuilder auth ) throws Exception;

	void configure( WebSecurity web ) throws Exception;

	void configure( HttpSecurity http ) throws Exception;
}
