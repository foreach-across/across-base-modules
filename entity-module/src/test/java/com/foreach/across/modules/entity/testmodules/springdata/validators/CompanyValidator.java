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
package com.foreach.across.modules.entity.testmodules.springdata.validators;

import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertNotNull;

/**
 * @author Arne Vandamme
 */
public class CompanyValidator implements Validator
{
	@EntityValidator
	private SmartValidator entityValidator;

	@Override
	public boolean supports( Class<?> clazz ) {
		return clazz.isAssignableFrom( Company.class );
	}

	@Override
	public void validate( Object target, Errors errors ) {
		assertNotNull( entityValidator );
	}
}
