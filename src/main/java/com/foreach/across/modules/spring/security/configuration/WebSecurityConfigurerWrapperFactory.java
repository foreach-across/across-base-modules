package com.foreach.across.modules.spring.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Creates a {@link com.foreach.across.modules.spring.security.configuration.WebSecurityConfigurerWrapper}
 * for a {@link com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurer} and
 * ensures it is autowired.
 *
 * @author Arne Vandamme
 * @since 1.0.3
 */
@Component
public class WebSecurityConfigurerWrapperFactory
{
	@Autowired
	private ApplicationContext applicationContext;

	public WebSecurityConfigurerWrapper createWrapper( SpringSecurityWebConfigurer configurer, int index ) {
		WebSecurityConfigurerWrapper wrapper = new WebSecurityConfigurerWrapper( configurer, index );
		applicationContext.getAutowireCapableBeanFactory().autowireBean( wrapper );

		return wrapper;
	}
}
