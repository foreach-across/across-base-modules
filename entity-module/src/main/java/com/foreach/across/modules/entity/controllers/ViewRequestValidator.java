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
package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Arne Vandamme
 */
public class ViewRequestValidator implements Validator
{
	@EntityValidator
	private Validator entityValidator;

	@Autowired
	private EntityRegistry entityRegistry;

	@Override
	public boolean supports( Class<?> clazz ) {
		return EntityViewRequest.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate( Object target, Errors errors ) {
		EntityViewRequest viewRequest = (EntityViewRequest) target;

		Object entity = viewRequest.getEntity();

		if ( entity != null ) {
			errors.pushNestedPath( "entity" );
			validatorForEntity( entity ).validate( entity, errors );
			errors.popNestedPath();
		}

		errors.pushNestedPath( "extensions" );
		entityValidator.validate( viewRequest.getExtensions(), errors );
		errors.popNestedPath();
	}

	private Validator validatorForEntity( Object entity ) {
		Validator validator = null;

		Class entityType = ClassUtils.getUserClass(entity);
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( entityType );

		if ( entityConfiguration != null ) {
			validator = entityConfiguration.getAttribute( Validator.class );
		}

		return validator != null ? validator : entityValidator;
	}
}
