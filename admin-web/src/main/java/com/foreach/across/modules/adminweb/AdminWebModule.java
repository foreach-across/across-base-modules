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

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.AcrossWebModule;

@AcrossDepends(
		required = { AcrossWebModule.NAME, SpringSecurityModule.NAME },
        optional = { "AcrossHibernateModule" }
)
public class AdminWebModule extends AcrossModule
{
	public static final String NAME = "AdminWebModule";
	public static final String RESOURCES = "adminweb";

	private String rootPath = "/admin";

	/**
	 * @return The root path for all AdminWebControllers.
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * Set the root path that all AdminWebController instances should use.  All request mappings
	 * will be prefixed with the path specified here.
	 *
	 * @param rootPath The root path for all AdminWebControllers.
	 * @see org.springframework.web.bind.annotation.RequestMapping
	 */
	public void setRootPath( String rootPath ) {
		this.rootPath = rootPath;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getResourcesKey() {
		return RESOURCES;
	}

	@Override
	public String getDescription() {
		return "Provides a basic administrative web interface with user authentication and authorization.";
	}
}
