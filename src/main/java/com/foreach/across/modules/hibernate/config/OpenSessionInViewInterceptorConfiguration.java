package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configures the OpenSessionInViewInterceptor if necessary.
 *
 * @author Arne Vandamme
 */
@AcrossDepends(required = "AcrossWebModule")
@AcrossCondition("${" + AcrossHibernateModuleSettings.OPEN_SESSION_IN_VIEW_INTERCEPTOR + ":false}")
@Configuration
public class OpenSessionInViewInterceptorConfiguration extends WebMvcConfigurerAdapter implements Ordered
{
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Environment environment;

	@Override
	public int getOrder() {
		return environment.getProperty( AcrossHibernateModuleSettings.OPEN_SESSION_IN_VIEW_INTERCEPTOR_ORDER,
		                                Integer.class, Ordered.HIGHEST_PRECEDENCE );
	}

	@Override
	public void addInterceptors( InterceptorRegistry registry ) {
		registry.addWebRequestInterceptor( openSessionInViewInterceptor() );
	}

	@Bean
	public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
		OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
		interceptor.setSessionFactory( sessionFactory );

		return interceptor;
	}
}
