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

package com.foreach.across.modules.hibernate.extensions;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@ConditionalOnAcrossModule("AcrossWebModule")
@Configuration
public class HibernatePersistenceContextInViewConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( HibernatePersistenceContextInViewConfiguration.class );

	@Configuration
	@ConditionalOnExpression("true == @moduleSettings.openInView and @moduleSettings.persistenceContextInView.handler.name() == 'FILTER'")
	public static class OpenSessionFactoryInViewFilterConfiguration
	{
		@Autowired
		private SessionFactory sessionFactory;

		@Autowired
		@Module(AcrossModule.CURRENT_MODULE)
		private AcrossModule currentModule;

		@Bean
		public OpenSessionInViewFilter openSessionInViewFilter() {
			return new OpenSessionInViewFilter()
			{
				@Override
				protected SessionFactory lookupSessionFactory() {
					return sessionFactory;
				}
			};
		}

		@Bean
		public FilterRegistrationBean<OpenSessionInViewFilter> openEntityManagerInViewFilterFilterRegistrationBean() {
			FilterRegistrationBean<OpenSessionInViewFilter> registrationBean = new FilterRegistrationBean<>();

			registrationBean.setName( currentModule.getName() + ".OpenSessionInViewFilter" );
			registrationBean.setFilter( openSessionInViewFilter() );
			registrationBean.setAsyncSupported( true );
			registrationBean.setDispatcherTypes( EnumSet.of( DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC ) );
			registrationBean.addUrlPatterns( "/*" );
			return registrationBean;
		}
	}

	@Configuration
	@ConditionalOnExpression("true == @moduleSettings.openInView and @moduleSettings.persistenceContextInView.handler.name() == 'INTERCEPTOR'")
	public static class OpenSessionFactoryInViewInterceptorConfiguration implements WebMvcConfigurer, Ordered
	{
		@Autowired
		private SessionFactory sessionFactory;

		@Autowired
		@Module(AcrossModule.CURRENT_MODULE)
		private AcrossHibernateModuleSettings settings;

		@Override
		public int getOrder() {
			return settings.getPersistenceContextInView().getOrder();
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
}