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

import com.foreach.across.modules.hibernate.types.BitFlag;
import com.foreach.across.modules.hibernate.types.HibernateBitFlag;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class TestHibernateBitFlag extends AbstractBaseIdLookup
{

	@Test
	public void testConvertIntFieldToEnumSet() throws Exception {
		TestHibernateBitFlagUserType userType = new TestHibernateBitFlagUserType();
		assertEquals( EnumSet.noneOf( BitValues.class ), mockResultSetAndTestValue( userType, null ) );
		assertEquals( EnumSet.noneOf( BitValues.class ), mockResultSetAndTestValue( userType, 0 ) );
		assertEquals( EnumSet.of( BitValues.RED ), mockResultSetAndTestValue( userType, 1 ) );
		assertEquals( EnumSet.of( BitValues.GREEN ), mockResultSetAndTestValue( userType, 2 ) );
		assertEquals( EnumSet.of( BitValues.RED, BitValues.GREEN ), mockResultSetAndTestValue( userType, 3 ) );
		assertEquals( EnumSet.of( BitValues.BLUE ), mockResultSetAndTestValue( userType, 4 ) );
		assertEquals( EnumSet.of( BitValues.BLUE, BitValues.RED ), mockResultSetAndTestValue( userType, 5 ) );
		assertEquals( EnumSet.of( BitValues.GREEN, BitValues.BLUE ), mockResultSetAndTestValue( userType, 6 ) );
		assertEquals( EnumSet.of( BitValues.RED, BitValues.GREEN, BitValues.BLUE ), mockResultSetAndTestValue( userType, 7 ) );

		// Invalid database values
		assertEquals( EnumSet.of( BitValues.RED, BitValues.GREEN, BitValues.BLUE ), mockResultSetAndTestValue(
				userType,
				Integer.MAX_VALUE ) );
	}

	@Test
	public void testConvertEnumSetToInt() throws Exception {
		TestHibernateBitFlagUserType userType = new TestHibernateBitFlagUserType();
		mockResultSetAndTestValueToInt( userType, 0, null );
		mockResultSetAndTestValueToInt( userType, 0, EnumSet.noneOf( BitValues.class ) );
		mockResultSetAndTestValueToInt( userType, 1, EnumSet.of( BitValues.RED ) );
		mockResultSetAndTestValueToInt( userType, 2, EnumSet.of( BitValues.GREEN ) );
		mockResultSetAndTestValueToInt( userType, 3, EnumSet.of( BitValues.RED, BitValues.GREEN ) );
		mockResultSetAndTestValueToInt( userType, 4, EnumSet.of( BitValues.BLUE ) );
		mockResultSetAndTestValueToInt( userType, 5, EnumSet.of( BitValues.BLUE, BitValues.RED ) );
		mockResultSetAndTestValueToInt( userType, 6, EnumSet.of( BitValues.GREEN, BitValues.BLUE ) );
		mockResultSetAndTestValueToInt( userType, 7, EnumSet.of( BitValues.RED, BitValues.GREEN, BitValues.BLUE ) );
	}

	public static enum BitValues implements BitFlag
	{
		RED( 1 ),
		GREEN( 2 ),
		BLUE( 4 );

		int id;

		BitValues( int id ) {
			this.id = id;
		}

		public Integer getId() {
			return this.id;
		}
	}

	private static class TestHibernateBitFlagUserType extends HibernateBitFlag<BitValues>
	{

		public TestHibernateBitFlagUserType() {
			super( BitValues.class );
		}
	}
}
