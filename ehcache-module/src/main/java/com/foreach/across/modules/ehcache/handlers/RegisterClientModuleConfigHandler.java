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

package com.foreach.across.modules.ehcache.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.ehcache.config.EhcacheClientModuleConfig;
import org.springframework.stereotype.Component;

/**
 * Ensures that every module that is bootstrapped after the current module has a
 * configuration class with the @EnableCaching annotation.
 */
@AcrossEventHandler
@Component
public class RegisterClientModuleConfigHandler
{
	@Event
	public void registerEhCacheClientModule( AcrossModuleBeforeBootstrapEvent event ) {
		event.addApplicationContextConfigurers( new AnnotatedClassConfigurer( EhcacheClientModuleConfig.class ) );
	}
}
