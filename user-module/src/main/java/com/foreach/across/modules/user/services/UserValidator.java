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
package com.foreach.across.modules.user.services;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator
{
	@Autowired
	private UserService userService;

	@Autowired
	private EmailValidator emailValidator;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Override
	public void validate( Object target, Errors errors ) {
		if ( supports( target.getClass() ) ) {
			User userDto = (User) target;
			if ( userService.isRequireEmailUnique() ) {
				User userByEmail = userService.getUserByEmail( userDto.getEmail() );
				if ( userByEmail != null && !userByEmail.getId().equals( userDto.getId() ) ) {
					errors.reject( null, "email already exists" );
				}
			}

			if ( userService.isUseEmailAsUsername() || userService.isRequireEmailUnique() ) {
				if ( StringUtils.isBlank( userDto.getEmail() ) ) {
					errors.rejectValue( "email", null, "email cannot be empty" );
				}
			}
			else {
				if ( StringUtils.isBlank( userDto.getUsername() ) ) {
					errors.rejectValue( "username", null, "username cannot be empty" );
				}
			}

			if ( StringUtils.contains( userDto.getUsername(), ' ' ) ) {
				errors.rejectValue( "username", null, "username cannot contain whitespaces" );
			}

			if ( !StringUtils.isBlank( userDto.getEmail() ) ) {
				if ( !emailValidator.isValid( userDto.getEmail(), null ) ) {
					errors.rejectValue( "email", null, "invalid email" );
				}
			}

			if ( !errors.hasErrors() ) {
				String principalName = userDto.getUsername();

				if ( userService.isUseEmailAsUsername() && StringUtils.isBlank( principalName ) ) {
					principalName = userDto.getEmail();
				}

				SecurityPrincipal principal = securityPrincipalService.getPrincipalByName( principalName );

				if ( principal != null && !isSamePrincipal( principal, userDto ) ) {
					errors.rejectValue( "username", null, "username is not available" );
				}
			}
		}
	}

	private boolean isSamePrincipal( SecurityPrincipal principal, User user ) {
		return principal instanceof User && ( (User) principal ).getId().equals( user.getId() );
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return User.class.isAssignableFrom( clazz );
	}
}
