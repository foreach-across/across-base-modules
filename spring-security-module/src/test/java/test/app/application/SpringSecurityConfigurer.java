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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Steven Gentens
 * @since 3.0.0
 */
// todo if we enable this configurer the default security backs off altogether; tests should be revised
@Configuration
public class SpringSecurityConfigurer
{
	@Configuration
	public static class BlockedSecurity
	{
		@Bean
		public SecurityFilterChain configure( HttpSecurity http ) throws Exception {
/*
			http.antMatcher( "/blocked" ).authorizeRequests().anyRequest().denyAll()
					.and().antMatcher( "/hello" ).authorizeRequests().anyRequest().authenticated().and().httpBasic()
					.and().antMatcher( "/thymeleaf-extras" ).authorizeRequests().anyRequest().authenticated().and().httpBasic()
					.and().antMatcher( "/current-user" ).authorizeRequests().anyRequest().authenticated().and().httpBasic().and().securityContext();
*/
			http.antMatcher( "/blocked" ).authorizeRequests().anyRequest().denyAll();
			return http.build();
		}
	}

	@Configuration
	public static class HelloSecurity
	{
		@Bean
		public DefaultSecurityFilterChain configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/hello" ).authorizeRequests().anyRequest().authenticated().and().httpBasic();
			return http.build();
		}
	}

	@Configuration
	public static class ThymeleafSecurity
	{
		@Bean
		public DefaultSecurityFilterChain configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/thymeleaf-extras" ).authorizeRequests().anyRequest().authenticated().and().httpBasic();
			return http.build();
		}
	}

	@Configuration
	public static class CurrentUserSecurity
	{
		@Bean
		public DefaultSecurityFilterChain configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/current-user" ).authorizeRequests().anyRequest().authenticated().and().httpBasic().and().securityContext();
			return http.build();
		}
	}
}
