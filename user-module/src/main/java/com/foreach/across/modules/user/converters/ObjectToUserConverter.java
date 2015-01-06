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
package com.foreach.across.modules.user.converters;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class ObjectToUserConverter implements Converter<Object, User>
{
	private final ConversionService conversionService;
	private final UserService userService;

	public ObjectToUserConverter( ConversionService conversionService, UserService userService ) {
		this.conversionService = conversionService;
		this.userService = userService;
	}

	@Override
	public User convert( Object source ) {
		long userId = conversionService.convert( source, Long.class );

		if ( userId != 0 ) {
			return userService.getUserById( userId );
		}

		return null;
	}
}
