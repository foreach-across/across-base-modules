package com.foreach.across.modules.spring.security.configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * Adapter implementation for the SpringSecurityWebConfigurer interface.  Override only
 * the methods you are interested in changing.
 *
 * @author Arne Vandamme
 * @since 1.0.3
 */
public class SpringSecurityWebConfigurerAdapter implements SpringSecurityWebConfigurer
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
