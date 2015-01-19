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
package com.foreach.across.modules.entity.registrars;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;

/**
 * Interface for automatic creating of EntityConfiguration.
 *
 * @author Arne Vandamme
 */
public interface EntityRegistrar
{
	/**
	 * Registers all entities from the given module.
	 *
	 * @param entityRegistry Registry the entities should be added to.
	 * @param moduleInfo     Module that should be scanned.
	 * @param beanRegistry   Bean registry of the AcrossContext the module belongs to.
	 */
	void registerEntities( MutableEntityRegistry entityRegistry,
	                       AcrossModuleInfo moduleInfo,
	                       AcrossContextBeanRegistry beanRegistry );
}
