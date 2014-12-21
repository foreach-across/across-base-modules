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

import com.foreach.across.modules.hibernate.types.HibernateIdLookup;
import com.foreach.across.modules.hibernate.types.IdLookup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestIdLookup extends AbstractBaseIdLookup
{
	@Test
	public void testConvertIntFieldToEnum() throws Exception {
		TestHibernateIdLookupUserType userType = new TestHibernateIdLookupUserType();
		assertEquals( null, mockResultSetAndTestValue( userType , null ) );
		assertEquals( IntegerValues.ONE, mockResultSetAndTestValue( userType , 1 ) );
		assertEquals( IntegerValues.TWO, mockResultSetAndTestValue( userType , 2 ) );
		assertEquals( IntegerValues.THREE, mockResultSetAndTestValue( userType , 3 ) );

	}

	@Test
	public void testConvertEnumToInt() throws Exception {
		TestHibernateIdLookupUserType userType = new TestHibernateIdLookupUserType();
		mockResultSetAndTestValueToInt( userType, 1, IntegerValues.ONE );
		mockResultSetAndTestValueToInt( userType, 2, IntegerValues.TWO );
		mockResultSetAndTestValueToInt( userType, 3, IntegerValues.THREE );
		mockResultSetAndTestValueToInt( userType, null, null );
	}

	public static enum IntegerValues implements IdLookup<Integer>
	{
		ONE( 1 ),
		TWO( 2 ),
		THREE( 3 );

		int id;

		IntegerValues( int id ) {
			this.id = id;
		}

		public Integer getId() {
			return this.id;
		}
	}

	private static class TestHibernateIdLookupUserType extends HibernateIdLookup<IntegerValues>
	{

		public TestHibernateIdLookupUserType() {
			super( IntegerValues.class );
		}
	}
}
