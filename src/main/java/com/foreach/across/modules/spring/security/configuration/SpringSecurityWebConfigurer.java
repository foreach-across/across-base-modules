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
