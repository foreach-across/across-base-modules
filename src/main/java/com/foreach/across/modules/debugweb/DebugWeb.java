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
package com.foreach.across.modules.debugweb;

import com.foreach.across.modules.web.context.PrefixingPathContext;

public final class DebugWeb extends PrefixingPathContext
{
	public static final String MODULE = DebugWebModule.NAME;
	public static final String VIEWS = "debugweb";

	public static final String CSS_MAIN = "/css/debugweb/debugweb.css";

	public static final String LAYOUT_TEMPLATE = "th/debugweb/layouts/debugPage";

	public static final String LAYOUT_BROWSER = "th/debugweb/layouts/acrossBrowser";
	public static final String VIEW_BROWSER_INFO = "th/debugweb/browser/info";
	public static final String VIEW_BROWSER_BEANS = "th/debugweb/browser/beans";
	public static final String VIEW_BROWSER_PROPERTIES = "th/debugweb/browser/properties";
	public static final String VIEW_BROWSER_HANDLERS = "th/debugweb/browser/handlers";
	public static final String VIEW_BROWSER_EVENTS = "th/debugweb/browser/events";

	public static final String VIEW_SPRING_BEANS = "th/debugweb/listBeans";
	public static final String VIEW_SPRING_INTERCEPTORS = "th/debugweb/listInterceptors";
	public static final String VIEW_PROPERTIES = "th/debugweb/listProperties";
	public static final String VIEW_APPLICATION_PROPERTIES = "th/debugweb/listApplicationProperties";
	public static final String VIEW_THREADS = "th/debugweb/listThreads";
	public static final String VIEW_MODULES = "th/debugweb/listAcrossModules";

	public static final String VIEW_SERVLET_FILTERS = "th/debugweb/servlet/filters";
	public static final String VIEW_SERVLET_SERVLETS = "th/debugweb/servlet/servlets";

	public DebugWeb( String prefix ) {
		super( prefix );
	}
}
