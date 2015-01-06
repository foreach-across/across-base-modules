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

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.services.GroupService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class ObjectToGroupConverter implements Converter<Object, Group>
{
	private final ConversionService conversionService;
	private final GroupService groupService;

	public ObjectToGroupConverter( ConversionService conversionService, GroupService groupService ) {
		this.conversionService = conversionService;
		this.groupService = groupService;
	}

	@Override
	public Group convert( Object source ) {
		long groupId = conversionService.convert( source, Long.class );

		if ( groupId != 0 ) {
			return groupService.getGroupById( groupId );
		}

		return null;
	}
}
