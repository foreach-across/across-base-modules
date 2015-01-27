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
package com.foreach.across.modules.entity.views.forms.elements;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestCommonFormElementTypeLookupStrategy
{
	private CommonFormElementTypeLookupStrategy strategy = new CommonFormElementTypeLookupStrategy();

	private EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
	private EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );

	@Before
	public void resetMocks() {
		reset( entityConfiguration, descriptor );
	}

	@Test
	public void hiddenType() {
		when( descriptor.isHidden() ).thenReturn( true );

		assertEquals( CommonFormElements.HIDDEN, lookup() );
	}

	@Test
	public void textboxType() {
		when( descriptor.isWritable() ).thenReturn( true );

		assertEquals( CommonFormElements.TEXTBOX, lookup() );
	}

	@Test
	public void unknownType() {
		assertNull( lookup() );
	}

	private String lookup() {
		return strategy.findElementType( entityConfiguration, descriptor );
	}
}
