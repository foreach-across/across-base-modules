package com.foreach.across.modules.hibernate;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import org.springframework.core.Ordered;

/**
 * @author Arne Vandamme
 */
public class AcrossHibernateModuleSettings extends AcrossModuleSettings
{
	/**
	 * Property to determine if an {@link org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor}
	 * should be configured if the AcrossWebModule is active.
	 * <p/>
	 * Value: boolean (default: false)
	 */
	public static final String OPEN_SESSION_IN_VIEW_INTERCEPTOR = "acrossHibernate.openSessionInViewInterceptor";

	/**
	 * Configure the order of the OpenSessionInViewInterceptor for this module (if created).
	 * <p/>
	 * Value: integer (default: {@link org.springframework.core.Ordered.HIGHEST_PRECEDENCE}
	 */
	public static final String OPEN_SESSION_IN_VIEW_INTERCEPTOR_ORDER =
			"acrossHibernate.openSessionInViewInterceptor.order";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( OPEN_SESSION_IN_VIEW_INTERCEPTOR, Boolean.class, false,
		                   "Should an OpenSessionInViewInterceptor be registered." );
		registry.register( OPEN_SESSION_IN_VIEW_INTERCEPTOR_ORDER, Integer.class, Ordered.HIGHEST_PRECEDENCE,
		                   "Configure the order of the OpenSessionInViewInterceptor for this module (if created)" );
	}
}
