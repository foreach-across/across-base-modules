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

package com.foreach.across.modules.hibernate.config.dynamic;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

public class PersistenceContextInViewConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( PersistenceContextInViewConfiguration.class );

	@ConditionalOnAcrossModule(allOf = "AcrossWebModule")
	public static class OpenSessionFactoryInViewFilterConfiguration extends AcrossWebDynamicServletConfigurer
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

		@Override
		protected void dynamicConfigurationAllowed( ServletContext servletContext ) throws ServletException {
			FilterRegistration.Dynamic registration = servletContext.addFilter(
					currentModule.getName() + ".OpenSessionInViewFilter", openSessionInViewFilter()
			);
			registration.setAsyncSupported( true );

			registration.addMappingForUrlPatterns( EnumSet.of(
					DispatcherType.REQUEST,
					DispatcherType.ERROR,
					DispatcherType.ASYNC
			                                       ),
			                                       false,
			                                       "/*" );
		}

		@Override
		protected void dynamicConfigurationDenied( ServletContext servletContext ) throws ServletException {
			LOG.warn( "Dynamic servlet filter configuration is not allowed.  " +
					          "The filter bean has been created but not registered in the ServletContext." );
		}
	}

	@ConditionalOnAcrossModule(allOf = "AcrossWebModule")
	public static class OpenSessionFactoryInViewInterceptorConfiguration extends WebMvcConfigurerAdapter implements Ordered
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