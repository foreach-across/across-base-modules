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

package com.foreach.across.modules.spring.security.test.application;

import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.stereotype.Component;

/**
 * @author Steven Gentens
 * @since 3.0.0-SNAPSHOT
 */
@Component
public class CustomSpringSecurityWebConfigurer implements SpringSecurityWebConfigurer
{
	@Override
	public boolean isDisableDefaults() {
		return false;
	}

	@Override
	public void configure( AuthenticationManagerBuilder auth ) throws Exception {

	}

	@Override
	public void configure( WebSecurity web ) throws Exception {

	}

	@Override
	public void configure( HttpSecurity http ) throws Exception {

	}
}
