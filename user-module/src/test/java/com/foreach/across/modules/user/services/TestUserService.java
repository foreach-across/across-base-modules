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

import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.common.test.MockedLoader;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.collections.Sets;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TestUserService.Config.class)
@DirtiesContext
public class TestUserService
{
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void resetMocks() {
		reset( userRepository );

		when( userRepository.save( any( User.class ) ) ).thenAnswer( new Answer<User>()
		{
			@Override
			public User answer( InvocationOnMock invocationOnMock ) throws Throwable {
				return (User) invocationOnMock.getArguments()[0];
			}
		} );
	}

	@Test
	public void createUserDto() throws Exception {
		User user = new User();

		user.setId( 1561L );
		user.setFirstName( "first name" );
		user.setLastName( "last name" );
		user.setUsername( "username" );
		user.setDisplayName( "my display name" );
		user.setEmail( "testcase@foreach.com" );
		String password = passwordEncoder.encode( "fuzih" );
		user.setPassword( password );

		user.setDeleted( true );
		user.setEmailConfirmed( true );
		user.setRestrictions( EnumSet.of( UserRestriction.CREDENTIALS_EXPIRED ) );

		Role role_1 = new Role( "role 1" );
		role_1.addPermission( "permission 1", "permission 2" );

		user.setRoles( Sets.newSet( role_1 ) );

		User userDto = user.toDto();

		assertEquals( Long.valueOf( 1561L ), user.getId() );
		assertEquals( "first name", userDto.getFirstName() );
		assertEquals( "last name", userDto.getLastName() );
		assertEquals( "username", userDto.getUsername() );
		assertEquals( "my display name", userDto.getDisplayName() );
		assertEquals( "testcase@foreach.com", userDto.getEmail() );
		assertEquals( null, userDto.getPassword() );

		assertEquals( true, userDto.isDeleted() );
		assertEquals( true, userDto.getEmailConfirmed() );
		assertEquals( EnumSet.of( UserRestriction.CREDENTIALS_EXPIRED ), userDto.getRestrictions() );
		assertEquals( true, userDto.hasRestrictions() );

		assertEquals( Sets.newSet( new SimpleGrantedAuthority( "role 1" ),
		                           new SimpleGrantedAuthority( "permission 1" ),
		                           new SimpleGrantedAuthority( "permission 2" ) ),
		              user.getAuthorities() );
	}

	@Test
	public void displayNameIsGeneratedFromFirstAndLastIfNotSpecified() {
		User dto = new User();
		dto.setFirstName( "first" );
		dto.setLastName( "last" );
		dto.setUsername( "user" );
		dto.setEmail( "email@valid.com" );
		dto.setPassword( "my-password" );

		userService.save( dto );

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass( User.class );
		verify( userRepository ).save( argument.capture() );

		User savedUser = argument.getValue();
		assertEquals( "first", savedUser.getFirstName() );
		assertEquals( "last", savedUser.getLastName() );
		assertEquals( "first last", savedUser.getDisplayName() );
	}

	@Test
	public void passwordGetsEncryptedIfSet() {
		User dto = new User();
		dto.setFirstName( "first" );
		dto.setLastName( "last" );
		dto.setUsername( "username" );
		dto.setEmail( "email@foo.bar" );
		dto.setPassword( "my-password" );

		userService.save( dto );

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass( User.class );
		verify( userRepository ).save( argument.capture() );

		User savedUser = argument.getValue();
		assertNotEquals( dto.getPassword(), savedUser.getPassword() );
		assertFalse( StringUtils.isBlank( savedUser.getPassword() ) );
	}

	@Test
	public void usernameIsRequired() {
		User dto = new User();
		dto.setPassword( "my-password" );

		try {
			userService.save( dto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			FieldError usernameError = (FieldError) errors.get( 0 );
			assertEquals( "user", usernameError.getObjectName() );
			assertEquals( "username", usernameError.getField() );
			assertEquals( "username cannot be empty", usernameError.getDefaultMessage() );
		}
	}

	@Test
	public void emailIsValidatedWhenSet() {
		User dto = new User();
		dto.setUsername( "someusername" );
		dto.setEmail( "bademailaddress" );
		dto.setPassword( "my-password" );

		try {
			userService.save( dto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );

			FieldError emailError = (FieldError) errors.get( 0 );
			assertEquals( "user", emailError.getObjectName() );
			assertEquals( "email", emailError.getField() );
			assertEquals( "invalid email", emailError.getDefaultMessage() );
		}
	}

	@Test(expected = UserModuleException.class)
	public void aNewUserAlwaysRequiresPassword() {
		User dto = new User();
		dto.setFirstName( "first" );
		dto.setLastName( "last" );
		dto.setUsername( "email" );

		userService.save( dto );
	}

	@Test
	public void emailAndUserNameAreConvertedToLowerCase() {
		User userDto = new User();
		userDto.setUsername( "AdMiN" );
		userDto.setEmail( "oThEr@EmAiL.EDU" );
		userDto.setPassword( "198(1!è!(§!ç(§ç" );

		userService.save( userDto );

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass( User.class );
		verify( userRepository ).save( argument.capture() );

		User createdUser = argument.getValue();
		assertEquals( "admin", createdUser.getUsername() );
		assertEquals( "other@email.edu", createdUser.getEmail() );
	}

	@Test
	public void emailWithSpacesThrowsValidationError() {
		User userDto = new User();
		userDto.setUsername( "admin" );
		userDto.setEmail( " oT hEr@EmA iL.EDU  " );
		userDto.setPassword( "198(1!è!(§!ç(§ç" );

		try {
			userService.save( userDto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			FieldError fieldError = (FieldError) errors.get( 0 );
			assertEquals( "email", fieldError.getField() );
		}
	}

	@Test
	public void userNameWithSpacesThrowsValidationError() {
		User userDto = new User();
		userDto.setUsername( "  A dM iN" );
		userDto.setEmail( "other@email.edu" );
		userDto.setPassword( "198(1!è!(§!ç(§ç" );

		try {
			userService.save( userDto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			FieldError fieldError = (FieldError) errors.get( 0 );
			assertEquals( "username", fieldError.getField() );
		}
	}

	@Test
	public void passwordIsNotModifiedIfNotSet() {
		User existing = new User();
		existing.setId( 321L );
		existing.setUsername( "uname" );
		existing.setEmail( "uname@test.com" );
		existing.setPassword( "my-existing-password" );

		User update = new User();
		update.setId( existing.getId() );
		update.setUsername( "other" );
		update.setEmail( "other@email.com" );

		when( userRepository.findOne( 321L ) ).thenReturn( existing );

		userService.save( update );

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass( User.class );
		verify( userRepository ).save( argument.capture() );

		User savedUser = argument.getValue();
		assertSame( existing, savedUser );
		assertEquals( "other", savedUser.getUsername() );
		assertEquals( "other@email.com", savedUser.getEmail() );
		assertEquals( "my-existing-password", savedUser.getPassword() );
	}

	@Test
	public void updatingUserWithZeroIdIsNotAllowed() {
		User dto = new User();
		dto.setNewEntityId( 0L );

		boolean failed = false;

		try {
			userService.save( dto );
		}
		catch ( UserModuleException ume ) {
			failed = true;
		}

		assertTrue( failed );
		verify( userRepository, never() ).findOne( any( Long.class ) );
	}

	@Test
	public void updatingUserThatDoesNotExistWillFail() {
		User dto = new User();
		dto.setId( 132L );

		boolean failed = false;

		try {
			userService.save( dto );
		}
		catch ( UserModuleException ume ) {
			failed = true;
		}

		assertTrue( failed );
		verify( userRepository ).findOne( 132L );
	}

	@Configuration
	static class Config
	{
		@Bean
		public UserService userService() {
			return new UserServiceImpl( passwordEncoder(), false, false );
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public UserValidator userValidator() {
			return new UserValidator();
		}

		@Bean
		public EmailValidator emailValidator() {
			return new EmailValidator();
		}
	}
}
