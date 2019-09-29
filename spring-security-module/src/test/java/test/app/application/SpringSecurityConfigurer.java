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

package test.app.application;

import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author Steven Gentens
 * @since 3.0.0
 */
// todo if we enable this configurer the default security backs off altogether; tests should be revised
@Configuration
public class SpringSecurityConfigurer
{
	@Configuration
	static class BlockedSecurity implements AcrossWebSecurityConfigurer
	{
		@Override
		public void configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/blocked" ).authorizeRequests().anyRequest().denyAll();
		}
	}

	@Configuration
	static class HelloSecurity implements AcrossWebSecurityConfigurer
	{
		@Override
		public void configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/hello" ).authorizeRequests().anyRequest().authenticated().and().httpBasic();
		}
	}

	@Configuration
	static class ThymeleafSecurity implements AcrossWebSecurityConfigurer
	{
		@Override
		public void configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/thymeleaf-extras" ).authorizeRequests().anyRequest().authenticated().and().httpBasic();
		}
	}
}
