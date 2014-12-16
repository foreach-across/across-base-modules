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

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;

@AcrossDepends(required = "AcrossWebModule")
public class DebugWebModule extends AcrossModule
{
	public static final String NAME = "DebugWebModule";
	public static final String RESOURCES = "debugweb";

	private String rootPath = "/debug";

	public void setRootPath( String rootPath ) {
		this.rootPath = rootPath;
	}

	public String getRootPath() {
		return rootPath;
	}

	/**
	 * @return Name of this module.  The spring bean should also be using this name.
	 */
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
		return "Provides a debug web path and functionality to easily register additional debug controllers.";
	}
}

