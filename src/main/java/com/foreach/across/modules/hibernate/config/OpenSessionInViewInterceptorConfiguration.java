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
package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configures the OpenSessionInViewInterceptor if necessary.
 *
 * @author Arne Vandamme
 */
@AcrossDepends(required = "AcrossWebModule")
@AcrossCondition("${" + AcrossHibernateModuleSettings.OPEN_SESSION_IN_VIEW_INTERCEPTOR + "}")
@Configuration
public class OpenSessionInViewInterceptorConfiguration extends WebMvcConfigurerAdapter implements Ordered
{
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private AcrossHibernateModuleSettings settings;

	@Override
	public int getOrder() {
		return settings.getProperty( AcrossHibernateModuleSettings.OPEN_SESSION_IN_VIEW_INTERCEPTOR_ORDER,
		                             Integer.class );
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
