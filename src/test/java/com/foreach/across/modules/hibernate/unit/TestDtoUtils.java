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
package com.foreach.across.modules.hibernate.unit;

import com.foreach.across.modules.hibernate.util.DtoUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestDtoUtils
{
	public static class Entity
	{
		private String name;
		private Set<String> values;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public Set<String> getValues() {
			return values;
		}

		public void setValues( Set<String> values ) {
			this.values = values;
		}
	}

	@Test
	public void collectionsAreNotCloned() {
		Entity one = new Entity();
		one.setName( "one" );
		one.setValues( Collections.singleton( "test" ) );

		Entity dto = DtoUtils.createDto( one );
		assertNotNull( dto );
		assertNotSame( one, dto );
		assertEquals( one.getName(), dto.getName() );
		assertEquals( one.getValues(), dto.getValues() );
	}
}
