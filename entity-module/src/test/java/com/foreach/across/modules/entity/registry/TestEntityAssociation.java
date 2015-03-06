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
package com.foreach.across.modules.entity.registry;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEntityAssociation
{
	@Test
	public void hiddenSemantics() {
		MutableEntityConfiguration source = mock( MutableEntityConfiguration.class );
		MutableEntityConfiguration target = mock( MutableEntityConfiguration.class );

		EntityAssociationImpl association = new EntityAssociationImpl( "assoc", source );
		association.setTargetEntityConfiguration( target );

		assertFalse( association.isHidden() );

		association.setHidden( true );
		assertTrue( association.isHidden() );

		association.setHidden( false );
		assertFalse( association.isHidden() );

		association.setHidden( null );

		when( source.isHidden() ).thenReturn( true );
		when( target.isHidden() ).thenReturn( false );
		assertTrue( association.isHidden() );

		when( source.isHidden() ).thenReturn( false );
		when( target.isHidden() ).thenReturn( true );
		assertTrue( association.isHidden() );

		when( source.isHidden() ).thenReturn( true );
		when( target.isHidden() ).thenReturn( true );
		assertTrue( association.isHidden() );

		association.setHidden( false );
		assertFalse( association.isHidden() );
	}
}
