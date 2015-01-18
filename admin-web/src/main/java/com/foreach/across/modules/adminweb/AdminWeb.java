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
package com.foreach.across.modules.adminweb;

import com.foreach.across.modules.web.context.PrefixingPathContext;
import org.springframework.beans.factory.annotation.Autowired;

public final class AdminWeb extends PrefixingPathContext
{
	public static final String MODULE = "AdminWebModule";

	public static final String LAYOUT_TEMPLATE_CSS = "/css/adminweb/adminweb.css";
	public static final String LAYOUT_TEMPLATE = "th/adminweb/layouts/adminPage";

	@Autowired
	private AdminWebModuleSettings settings;

	public AdminWeb( String prefix ) {
		super( prefix );
	}

	public String getTitle() {
		return settings.getTitle();
	}

	public AdminWebModuleSettings getSettings() {
		return settings;
	}
}
