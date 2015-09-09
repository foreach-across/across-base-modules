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
	 * Value: integer (default: {@link org.springframework.core.Ordered#HIGHEST_PRECEDENCE}
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
