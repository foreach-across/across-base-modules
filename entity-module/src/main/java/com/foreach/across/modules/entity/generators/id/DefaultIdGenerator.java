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
package com.foreach.across.modules.entity.generators.id;

import com.foreach.across.modules.entity.generators.EntityIdGenerator;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;

import java.io.Serializable;

@Deprecated
public class DefaultIdGenerator implements EntityIdGenerator<Object>
{
	@Override
	public Serializable getId( Object entity ) {
		if ( entity instanceof IdBasedEntity ) {
			return ( (IdBasedEntity) entity ).getId();
		}
		else if ( entity instanceof Enum ) {
			return ( (Enum) entity ).name();
		}

		return entity.toString();
	}
}
