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
package com.foreach.across.modules.adminweb.config.support;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfigurerAdapter;

/**
 * {@link com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfigurer} adapter tied
 * to the default adminWeb handler mapping.  Implementations do not need to override the {@link #supports(String)}
 * method anymore.
 *
 * @author Arne Vandamme
 */
public class AdminWebConfigurerAdapter extends PrefixingHandlerMappingConfigurerAdapter
{
	@Override
	public boolean supports( String mapperName ) {
		return AdminWebModule.NAME.equals( mapperName );
	}
}
