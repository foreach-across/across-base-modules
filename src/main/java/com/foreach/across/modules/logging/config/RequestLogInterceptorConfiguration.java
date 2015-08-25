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
package com.foreach.across.modules.logging.config;

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfigurer;
import com.foreach.across.modules.web.mvc.InterceptorRegistry;
import com.foreach.common.web.logging.RequestLogInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * @author Andy Somers
 */
@Configuration
@AcrossDepends(required = AcrossWebModule.NAME)
@AcrossCondition("settings.requestLogger == T(com.foreach.across.modules.logging.config.RequestLogger).INTERCEPTOR")
public class RequestLogInterceptorConfiguration implements PrefixingHandlerMappingConfigurer
{
	@Override
	public boolean supports( String mapperName ) {
		return true;
	}

	@Override
	public void addInterceptors( InterceptorRegistry interceptorRegistry ) {
		interceptorRegistry.addFirst( new RequestLogInterceptor() );
	}
}
