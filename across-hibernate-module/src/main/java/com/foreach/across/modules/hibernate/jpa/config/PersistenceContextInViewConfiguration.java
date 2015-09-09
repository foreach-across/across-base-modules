package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

public class PersistenceContextInViewConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( PersistenceContextInViewConfiguration.class );

	@Configuration
	@AcrossDepends(required = "AcrossWebModule")
	@AcrossCondition("settings.persistenceContextInView.toString() == 'FILTER'")
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
	@AcrossDepends(required = "AcrossWebModule")
	@AcrossCondition("settings.persistenceContextInView.toString() == 'INTERCEPTOR'")
	public static class OpenEntityManagerInViewInterceptorConfiguration extends WebMvcConfigurerAdapter implements Ordered
	{
		@Autowired
		private EntityManagerFactory entityManagerFactory;

		@Autowired
		@Module(AcrossModule.CURRENT_MODULE)
		private AcrossHibernateModuleSettings settings;

		@Override
		public int getOrder() {
			return settings.getPersistenceContextInViewOrder();
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