package com.foreach.across.modules.spring.security.config;

import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Automatically registers the Spring security filter chain if possible.
 * Performs more or less the same actions as the specialized WebApplicationInitializer that needs
 * to be provided manually.
 *
 * @author Arne Vandamme
 * @see org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer
 */
@Configuration
public class DynamicSecurityFilterConfiguration extends AcrossWebDynamicServletConfigurer
{
	private static final Logger LOG = LoggerFactory.getLogger( DynamicSecurityFilterConfiguration.class );

	private static final String SERVLET_CONTEXT_PREFIX = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.";

	public static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";

	@Override
	protected void dynamicConfigurationAllowed( ServletContext servletContext ) throws ServletException {
		LOG.info( "Auto-registering Spring security filter chain" );

		beforeSpringSecurityFilterChain( servletContext );
		if ( enableHttpSessionEventPublisher() ) {
			servletContext.addListener( "org.springframework.security.web.session.HttpSessionEventPublisher" );
		}
		servletContext.setSessionTrackingModes( getSessionTrackingModes() );
		insertSpringSecurityFilterChain( servletContext );
		afterSpringSecurityFilterChain( servletContext );
	}

	@Override
	protected void dynamicConfigurationDenied( ServletContext servletContext ) throws ServletException {
		LOG.warn(
				"Could not dynamically register Spring security filter chain - please configure a AbstractSecurityWebApplicationInitializer manually" );
	}

	/**
	 * Override this if {@link org.springframework.security.web.session.HttpSessionEventPublisher} should be added as a
	 * listener. This should be true, if session management has specified a
	 * maximum number of sessions.
	 *
	 * @return true to add {@link org.springframework.security.web.session.HttpSessionEventPublisher}, else false
	 */
	protected boolean enableHttpSessionEventPublisher() {
		return false;
	}

	/**
	 * Registers the springSecurityFilterChain
	 *
	 * @param servletContext the {@link ServletContext}
	 */
	private void insertSpringSecurityFilterChain( ServletContext servletContext ) {
		String filterName = DEFAULT_FILTER_NAME;
		DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy( filterName );
		String contextAttribute = getWebApplicationContextAttribute();
		if ( contextAttribute != null ) {
			springSecurityFilterChain.setContextAttribute( contextAttribute );
		}
		registerFilter( servletContext, true, filterName, springSecurityFilterChain );
	}

	/**
	 * Inserts the provided {@link javax.servlet.Filter}s before existing {@link javax.servlet.Filter}s
	 * using default generated names, {@link #getSecurityDispatcherTypes()}, and
	 * {@link #isAsyncSecuritySupported()}.
	 *
	 * @param servletContext the {@link ServletContext} to use
	 * @param filters        the {@link javax.servlet.Filter}s to register
	 */
	protected final void insertFilters( ServletContext servletContext, Filter... filters ) {
		registerFilters( servletContext, true, filters );
	}

	/**
	 * Inserts the provided {@link Filter}s after existing {@link Filter}s
	 * using default generated names, {@link #getSecurityDispatcherTypes()}, and
	 * {@link #isAsyncSecuritySupported()}.
	 *
	 * @param servletContext the {@link ServletContext} to use
	 * @param filters        the {@link Filter}s to register
	 */
	protected final void appendFilters( ServletContext servletContext, Filter... filters ) {
		registerFilters( servletContext, false, filters );
	}

	/**
	 * Registers the provided {@link Filter}s using default generated names,
	 * {@link #getSecurityDispatcherTypes()}, and
	 * {@link #isAsyncSecuritySupported()}.
	 *
	 * @param servletContext           the {@link ServletContext} to use
	 * @param insertBeforeOtherFilters if true, will insert the provided {@link Filter}s before other
	 *                                 {@link Filter}s. Otherwise, will insert the {@link Filter}s
	 *                                 after other {@link Filter}s.
	 * @param filters                  the {@link Filter}s to register
	 */
	private void registerFilters( ServletContext servletContext, boolean insertBeforeOtherFilters, Filter... filters ) {
		Assert.notEmpty( filters, "filters cannot be null or empty" );

		for ( Filter filter : filters ) {
			if ( filter == null ) {
				throw new IllegalArgumentException( "filters cannot contain null values. Got " + Arrays.asList(
						filters ) );
			}
			String filterName = Conventions.getVariableName( filter );
			registerFilter( servletContext, insertBeforeOtherFilters, filterName, filter );
		}
	}

	/**
	 * Registers the provided filter using the {@link #isAsyncSecuritySupported()} and {@link #getSecurityDispatcherTypes()}.
	 *
	 * @param servletContext
	 * @param insertBeforeOtherFilters should this Filter be inserted before or after other {@link Filter}
	 * @param filterName
	 * @param filter
	 */
	private void registerFilter( ServletContext servletContext,
	                             boolean insertBeforeOtherFilters,
	                             String filterName,
	                             Filter filter ) {
		FilterRegistration.Dynamic registration = servletContext.addFilter( filterName, filter );
		if ( registration == null ) {
			throw new IllegalStateException(
					"Duplicate Filter registration for '" + filterName + "'. Check to ensure the Filter is only configured once." );
		}
		registration.setAsyncSupported( isAsyncSecuritySupported() );
		EnumSet<DispatcherType> dispatcherTypes = getSecurityDispatcherTypes();
		registration.addMappingForUrlPatterns( dispatcherTypes, !insertBeforeOtherFilters, "/*" );
	}

	/**
	 * Returns the {@link DelegatingFilterProxy#getContextAttribute()} or null
	 * if the parent {@link org.springframework.context.ApplicationContext} should be used. The default
	 * behavior is to use the parent {@link org.springframework.context.ApplicationContext}.
	 * <p/>
	 * <p>
	 * If {@link #getDispatcherWebApplicationContextSuffix()} is non-null the
	 * {@link org.springframework.web.context.WebApplicationContext} for the Dispatcher will be used. This means
	 * the child {@link org.springframework.context.ApplicationContext} is used to look up the
	 * springSecurityFilterChain bean.
	 * </p>
	 *
	 * @return the {@link DelegatingFilterProxy#getContextAttribute()} or null
	 * if the parent {@link org.springframework.context.ApplicationContext} should be used
	 */
	private String getWebApplicationContextAttribute() {
		String dispatcherServletName = getDispatcherWebApplicationContextSuffix();
		if ( dispatcherServletName == null ) {
			return null;
		}
		return SERVLET_CONTEXT_PREFIX + dispatcherServletName;
	}

	/**
	 * Determines how a session should be tracked. By default,
	 * {@link SessionTrackingMode#COOKIE} is used.
	 * <p/>
	 * <p>
	 * Note that {@link SessionTrackingMode#URL} is intentionally omitted to
	 * help protected against <a
	 * href="http://en.wikipedia.org/wiki/Session_fixation">session fixation
	 * attacks</a>. {@link SessionTrackingMode#SSL} is omitted because SSL
	 * configuration is required for this to work.
	 * </p>
	 * <p/>
	 * <p>
	 * Subclasses can override this method to make customizations.
	 * </p>
	 *
	 * @return
	 */
	protected Set<SessionTrackingMode> getSessionTrackingModes() {
		return EnumSet.of( SessionTrackingMode.COOKIE );
	}

	/**
	 * Return the <servlet-name> to use the DispatcherServlet's
	 * {@link org.springframework.web.context.WebApplicationContext} to find the {@link DelegatingFilterProxy}
	 * or null to use the parent {@link org.springframework.context.ApplicationContext}.
	 * <p/>
	 * <p>
	 * For example, if you are using AbstractDispatcherServletInitializer or
	 * AbstractAnnotationConfigDispatcherServletInitializer and using the
	 * provided Servlet name, you can return "dispatcher" from this method to
	 * use the DispatcherServlet's {@link org.springframework.web.context.WebApplicationContext}.
	 * </p>
	 *
	 * @return the <servlet-name> of the DispatcherServlet to use its
	 * {@link org.springframework.web.context.WebApplicationContext} or null (default) to use the parent
	 * {@link org.springframework.context.ApplicationContext}.
	 */
	protected String getDispatcherWebApplicationContextSuffix() {
		return null;
	}

	/**
	 * Invoked before the springSecurityFilterChain is added.
	 *
	 * @param servletContext the {@link ServletContext}
	 */
	protected void beforeSpringSecurityFilterChain( ServletContext servletContext ) {

	}

	/**
	 * Invoked after the springSecurityFilterChain is added.
	 *
	 * @param servletContext the {@link ServletContext}
	 */
	protected void afterSpringSecurityFilterChain( ServletContext servletContext ) {

	}

	/**
	 * Get the {@link DispatcherType} for the springSecurityFilterChain.
	 *
	 * @return
	 */
	protected EnumSet<DispatcherType> getSecurityDispatcherTypes() {
		return EnumSet.of( DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC );
	}

	/**
	 * Determine if the springSecurityFilterChain should be marked as supporting
	 * asynch. Default is true.
	 *
	 * @return true if springSecurityFilterChain should be marked as supporting
	 * asynch
	 */
	protected boolean isAsyncSecuritySupported() {
		return true;
	}
}
