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

import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserPropertiesService userPropertiesService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	private final PasswordEncoder passwordEncoder;
	private final boolean useEmailAsUsername;
	private final boolean requireEmailUnique;

	public UserServiceImpl( PasswordEncoder passwordEncoder, boolean useEmailAsUsername, boolean requireEmailUnique ) {
		Assert.notNull( passwordEncoder, "A UserService must be configured with a valid PasswordEncoder" );
		this.passwordEncoder = passwordEncoder;
		this.useEmailAsUsername = useEmailAsUsername;
		this.requireEmailUnique = requireEmailUnique;
	}

	@PostConstruct
	protected void validateServiceConfiguration() {
		if ( useEmailAsUsername && !requireEmailUnique ) {
			throw new RuntimeException(
					UserModuleSettings.REQUIRE_EMAIL_UNIQUE + " must be TRUE if " + UserModuleSettings.USE_EMAIL_AS_USERNAME + " is TRUE" );
		}
	}

	public boolean isUseEmailAsUsername() {
		return useEmailAsUsername;
	}

	public boolean isRequireEmailUnique() {
		return requireEmailUnique;
	}

	@Override
	public Collection<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById( long id ) {
		return userRepository.findOne( id );
	}

	@Override
	public User getUserByEmail( String email ) {
		return userRepository.findByEmail( email );
	}

	@Override
	public User getUserByUsername( String username ) {
		return userRepository.findByUsername( username );
	}

	@Override
	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public User save( User userDto ) {
		User user;

		boolean isPrincipalRename = false;
		String oldPrincipalName = null;

		if ( userDto.isNew() ) {
			if ( StringUtils.isBlank( userDto.getPassword() ) ) {
				throw new UserModuleException( "A new user always requires a non-blank password to be set." );
			}

			user = new User();
		}
		else {
			long existingUserId = userDto.getId();

			if ( existingUserId == 0 ) {
				throw new UserModuleException(
						"Impossible to update a user with id 0, 0 is a special id that should never be used for persisted entities." );
			}

			user = getUserById( existingUserId );

			if ( user == null ) {
				throw new UserModuleException(
						"Attempt to update user with id " + existingUserId + " but that user does not exist" );
			}
		}

		if ( useEmailAsUsername ) {
			if ( StringUtils.isBlank( userDto.getUsername() ) ) {
				userDto.setUsername( userDto.getEmail() );
			}

			// Update username to new email if it is modified and username was the old email
			if ( !userDto.isNew()
					&& !StringUtils.equals( userDto.getEmail(), user.getEmail() )
					&& StringUtils.equals( userDto.getUsername(), user.getEmail() ) ) {
				userDto.setUsername( userDto.getEmail() );
			}
		}

		if ( !userDto.isNew() && !StringUtils.equalsIgnoreCase( userDto.getUsername(), user.getUsername() ) ) {
			isPrincipalRename = true;
			oldPrincipalName = user.getPrincipalName();
		}

		Errors errors = new BeanPropertyBindingResult( userDto, "user" );
		userValidator.validate( userDto, errors );

		if ( errors.hasErrors() ) {
			throw new UserValidationException(
					"Failed to validate User, [" + errors.getErrorCount() + "] validation errors: " + StringUtils.join(
							errors.getAllErrors(), System.lineSeparator() ),
					errors.getAllErrors() );
		}

		BeanUtils.copyProperties( userDto, user, "password" );

		// Only modify password if password on the dto is not blank
		if ( !StringUtils.isBlank( userDto.getPassword() ) ) {
			user.setPassword( passwordEncoder.encode( userDto.getPassword() ) );
		}

		if ( StringUtils.isBlank( user.getDisplayName() ) ) {
			user.setDisplayName( String.format( "%s %s", user.getFirstName(), user.getLastName() ).trim() );
		}

		User saved = userRepository.save( user );

		BeanUtils.copyProperties( saved, userDto, "password" );

		if ( isPrincipalRename ) {
			securityPrincipalService.publishRenameEvent( oldPrincipalName, user.getPrincipalName() );
		}

		return saved;
	}

	@Override
	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public void delete( long userId ) {
		User user = userRepository.findOne( userId );
		deleteProperties( userId );
		userRepository.delete( user );
	}

	@Override
	public void deleteProperties( User user ) {
		deleteProperties( user.getId() );
	}

	@Override
	public void deleteProperties( long userId ) {
		userPropertiesService.deleteProperties( userId );
	}

	@Override
	public UserProperties getProperties( User user ) {
		return userPropertiesService.getProperties( user.getId() );
	}

	@Override
	public void saveProperties( UserProperties userProperties ) {
		userPropertiesService.saveProperties( userProperties );
	}

	@Transactional(value = HibernateJpaConfiguration.TRANSACTION_MANAGER, readOnly = true)
	@Override
	public Collection<User> getUsersWithPropertyValue( String propertyName, Object propertyValue ) {
		Collection<Long> userIds = userPropertiesService.getEntityIdsForPropertyValue( propertyName, propertyValue );

		if ( userIds.isEmpty() ) {
			return Collections.emptyList();
		}

		return userRepository.findAll( userIds );
	}

	@Override
	public Collection<User> findUsers( Predicate predicate ) {
		return (Collection<User>) userRepository.findAll( predicate );
	}

	@Override
	public Page<User> findUsers( Predicate predicate, Pageable pageable ) {
		return userRepository.findAll( predicate, pageable );
	}
}
