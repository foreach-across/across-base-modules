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
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.dto.UserDto;

import java.util.Collection;

public interface UserService extends UserPropertiesService
{
	boolean isUseEmailAsUsername();

	boolean isRequireEmailUnique();

	Collection<User> getUsers();

	User getUserById( long id );

	User getUserByEmail( String email );

	User getUserByUsername( String username );

	UserDto createUserDto( User user );

	User save( UserDto user );

	void delete( long id );

	UserProperties getProperties( User user );

	UserProperties getProperties( UserDto userDto );
}
