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
import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.persistence.EntityManagerFactory;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

@ConditionalOnAcrossModule("AcrossWebModule")
@Configuration
public class JpaPersistenceContextInViewConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( JpaPersistenceContextInViewConfiguration.class );

	@Configuration
	@ConditionalOnExpression("true == @moduleSettings.openInView and @moduleSettings.persistenceContextInView.handler.name() == 'FILTER'")
	public static class OpenEntityManagerInViewFilterConfiguration extends AcrossWebDynamicServletConfigurer
	{
		@Autowired
		private EntityManagerFactory entityManagerFactory;

		@Autowired
		@Module(AcrossModule.CURRENT_MODULE)
		private AcrossModule currentModule;

		@Bean
		public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
			return new OpenEntityManagerInViewFilter()
			{
				@Override
				protected EntityManagerFactory lookupEntityManagerFactory() {
					return entityManagerFactory;
				}
			};
		}

		@Override
		protected void dynamicConfigurationAllowed( ServletContext servletContext ) throws ServletException {
			FilterRegistration.Dynamic registration = servletContext.addFilter(
					currentModule.getName() + ".OpenEntityManagerInViewFilter", openEntityManagerInViewFilter()
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

	@Configuration
	@ConditionalOnExpression("true == @moduleSettings.openInView and @moduleSettings.persistenceContextInView.handler.name() == 'INTERCEPTOR'")
	public static class OpenEntityManagerInViewInterceptorConfiguration extends WebMvcConfigurerAdapter implements Ordered
	{
		@Autowired
		private EntityManagerFactory entityManagerFactory;

		@Autowired
		@Module(AcrossModule.CURRENT_MODULE)
		private AcrossHibernateModuleSettings settings;

		@Override
		public int getOrder() {
			return settings.getPersistenceContextInView().getOrder();
		}

		@Override
		public void addInterceptors( InterceptorRegistry registry ) {
			registry.addWebRequestInterceptor( openEntityManagerInViewInterceptor() );
		}

		@Bean
		public OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor() {
			OpenEntityManagerInViewInterceptor interceptor = new OpenEntityManagerInViewInterceptor();
			interceptor.setEntityManagerFactory( entityManagerFactory );

			return interceptor;
		}
	}
}