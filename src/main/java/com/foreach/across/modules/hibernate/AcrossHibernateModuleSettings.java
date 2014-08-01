package com.foreach.across.modules.hibernate;

/**
 * @author Arne Vandamme
 */
public interface AcrossHibernateModuleSettings
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
}
