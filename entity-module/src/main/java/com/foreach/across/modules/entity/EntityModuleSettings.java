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
package com.foreach.across.modules.entity;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;

/**
 * @author Arne Vandamme
 */
public class EntityModuleSettings extends AcrossModuleSettings
{
	public static final String REGISTER_ENTITY_VALIDATOR_FOR_MVC = "entityModule.entityValidator.registerForMvc";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( REGISTER_ENTITY_VALIDATOR_FOR_MVC, Boolean.class, true,
		                   "Should the entity Validator instance be registered as the default validator for MVC databinding.");
	}

	public boolean shouldRegisterEntityValidatorForMvc() {
		return getProperty( REGISTER_ENTITY_VALIDATOR_FOR_MVC, Boolean.class );
	}
}
