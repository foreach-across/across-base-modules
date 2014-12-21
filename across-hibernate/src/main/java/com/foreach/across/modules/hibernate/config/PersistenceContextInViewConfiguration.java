package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
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

	@Configuration
	@AcrossDepends(required = "AcrossWebModule")
	@AcrossCondition("settings.persistenceContextInView.toString() == 'FILTER'")
	public static class OpenEntityManagerInViewFilterConfiguration extends AcrossWebDynamicServletConfigurer
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

	@Configuration
	@AcrossDepends(required = "AcrossWebModule")
	@AcrossCondition("settings.persistenceContextInView.toString() == 'INTERCEPTOR'")
	public static class OpenEntityManagerInViewInterceptorConfiguration extends WebMvcConfigurerAdapter implements Ordered
	{
		@Autowired
		private SessionFactory sessionFactory;

		@Autowired
		@Module(AcrossModule.CURRENT_MODULE)
		private AcrossHibernateModuleSettings settings;

		@Override
		public int getOrder() {
			return settings.getPersistenceContextInViewOrder();
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