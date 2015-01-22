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

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.common.test.MockedLoader;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TestUserServiceWithEmailAsUserName.Config.class)
@DirtiesContext
public class TestUserServiceWithEmailAsUserName
{
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
	public void creatingUserFailsWhenEmailAlreadyExists() throws Exception {

		long firstUserId = 498L;
		User userDto = new User();
		userDto.setId( firstUserId );
		userDto.setEmail( "498@email.com" );

		User user = new User();
		user.setId( firstUserId );

		User otherUser = new User();
		otherUser.setId( 123L );

		when( userRepository.findOne( firstUserId ) ).thenReturn( user );
		when( userRepository.findByEmail( "498@email.com" ) ).thenReturn( otherUser );

		try {
			userService.save( userDto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			ObjectError fieldError = errors.get( 0 );
			assertEquals( "user", fieldError.getObjectName() );
			assertEquals( "email already exists", fieldError.getDefaultMessage() );
		}

	}

	@Test
	public void creatingUserFailsWhenEmailIsNotSpecified() throws Exception {
		User userDto = new User();
		userDto.setPassword( "password" );
		try {
			userService.save( userDto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			FieldError fieldError = (FieldError) errors.get( 0 );
			assertEquals( "user", fieldError.getObjectName() );
			assertEquals( "email", fieldError.getField() );
			assertEquals( "email cannot be empty", fieldError.getDefaultMessage() );
		}
	}

	@Test
	public void creatingUserWithUsernameIsStillAllowed() throws Exception {
		User userDto = new User();
		userDto.setUsername( "someusername" );
		userDto.setPassword( "password" );
		userDto.setEmail( "test@email.com" );

		userService.save( userDto );
	}

	@Test
	public void creatingOrUpdatingSetsEmailAsUsername() throws Exception {
		User userDto = new User();
		userDto.setPassword( "password" );
		userDto.setEmail( "test@there.com" );

		userService.save( userDto );
		assertEquals( "test@there.com", userDto.getUsername() );
	}

//	@Test
//	public void creatingExistingUserDoesntErrorWhenThereIsAnErrorWithEmail() {
//		String email = "fooexample.com";
//		User dummy = new User();
//		dummy.setId( 5 );
//		dummy.setEmail( email );
//		dummy.setUsername( email );
//
//		UserDto dto = new UserDto();
//		dto.setEmail( email );
//		dto.setPassword( "foo" );
//
//		when( userRepository.getByEmail( email ) ).thenReturn( dummy );
//
//		try {
//			userService.save( dto );
//		}
//		catch ( UserValidationException uve ) {
//			List<ObjectError> errors = uve.getErrors();
//			assertEquals( 2, errors.size() );
//
//			ObjectError emailExists = errors.get( 0 );
//			assertEquals( "email already exists", emailExists.getDefaultMessage() );
//
//			FieldError invalidEmail = (FieldError) errors.get( 1 );
//			assertEquals( "email", invalidEmail.getField() );
//			assertEquals( "invalid email", invalidEmail.getDefaultMessage() );
//		}
//	}

	@Configuration
	static class Config
	{
		@Bean
		public UserService userService() {
			return spy( new UserServiceImpl( passwordEncoder(), true, true ) );
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