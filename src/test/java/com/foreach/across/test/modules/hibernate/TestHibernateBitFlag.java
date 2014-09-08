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
package com.foreach.across.test.modules.hibernate;

import com.foreach.across.modules.hibernate.types.BitFlag;
import com.foreach.across.modules.hibernate.types.HibernateBitFlag;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TestHibernateBitFlag
{

	@Test
	public void testConvertIntFieldToEnumSet() throws Exception {
		assertEquals( EnumSet.noneOf( BitValues.class ), mockResultSetAndTestValue( null ) );
		assertEquals( EnumSet.noneOf( BitValues.class ), mockResultSetAndTestValue( 0 ) );
		assertEquals( EnumSet.of( BitValues.RED ), mockResultSetAndTestValue( 1 ) );
		assertEquals( EnumSet.of( BitValues.GREEN ), mockResultSetAndTestValue( 2 ) );
		assertEquals( EnumSet.of( BitValues.RED, BitValues.GREEN ), mockResultSetAndTestValue( 3 ) );
		assertEquals( EnumSet.of( BitValues.BLUE ), mockResultSetAndTestValue( 4 ) );
		assertEquals( EnumSet.of( BitValues.BLUE, BitValues.RED ), mockResultSetAndTestValue( 5 ) );
		assertEquals( EnumSet.of( BitValues.GREEN, BitValues.BLUE ), mockResultSetAndTestValue( 6 ) );
		assertEquals( EnumSet.of( BitValues.RED, BitValues.GREEN, BitValues.BLUE ), mockResultSetAndTestValue( 7 ) );

		// Invalid database values
		assertEquals( EnumSet.of( BitValues.RED, BitValues.GREEN, BitValues.BLUE ), mockResultSetAndTestValue(
				Integer.MAX_VALUE ) );
	}

	@Test
	public void testConvertEnumSetToInt() throws Exception {
		mockResultSetAndTestValueToInt( 0, null );
		mockResultSetAndTestValueToInt( 0, EnumSet.noneOf( BitValues.class ) );
		mockResultSetAndTestValueToInt( 1, EnumSet.of( BitValues.RED ) );
		mockResultSetAndTestValueToInt( 2, EnumSet.of( BitValues.GREEN ) );
		mockResultSetAndTestValueToInt( 3, EnumSet.of( BitValues.RED, BitValues.GREEN ) );
		mockResultSetAndTestValueToInt( 4, EnumSet.of( BitValues.BLUE ) );
		mockResultSetAndTestValueToInt( 5, EnumSet.of( BitValues.BLUE, BitValues.RED ) );
		mockResultSetAndTestValueToInt( 6, EnumSet.of( BitValues.GREEN, BitValues.BLUE ) );
		mockResultSetAndTestValueToInt( 7, EnumSet.of( BitValues.RED, BitValues.GREEN, BitValues.BLUE ) );
	}

	private Object mockResultSetAndTestValue( Integer rowValue ) throws SQLException {
		TestHibernateBitFlagUserType userType = new TestHibernateBitFlagUserType();
		ResultSet resultSet = mock( ResultSet.class );
		SessionImplementor sessionImplementor = mock( SessionImplementor.class );
		SessionFactoryImplementor sessionFactoryImplementor = mock( SessionFactoryImplementor.class );
		when( sessionImplementor.getFactory() ).thenReturn( sessionFactoryImplementor );
		when( sessionFactoryImplementor.getDialect() ).thenReturn( new HSQLDialect() );
		String[] names = new String[] { "someColumn" };
		if ( rowValue == null ) {
			when( resultSet.wasNull() ).thenReturn( true );
		}
		else {
			when( resultSet.getInt( "someColumn" ) ).thenReturn( rowValue );
		}

		return userType.nullSafeGet( resultSet, names, sessionImplementor, new Object() );
	}

	private void mockResultSetAndTestValueToInt( Integer expectedValue,
	                                             EnumSet<BitValues> rowValue ) throws SQLException {
		TestHibernateBitFlagUserType userType = new TestHibernateBitFlagUserType();
		PreparedStatement preparedStatement = mock( PreparedStatement.class );
		SessionImplementor sessionImplementor = mock( SessionImplementor.class );
		SessionFactoryImplementor sessionFactoryImplementor = mock( SessionFactoryImplementor.class );
		when( sessionImplementor.getFactory() ).thenReturn( sessionFactoryImplementor );
		when( sessionFactoryImplementor.getDialect() ).thenReturn( new HSQLDialect() );

		userType.nullSafeSet( preparedStatement, rowValue, 3, sessionImplementor );

		verify( preparedStatement, times( 1 ) ).setInt( eq( 3 ), eq( expectedValue ) );
	}

	public static enum BitValues implements BitFlag
	{
		RED( 1 ),
		GREEN( 2 ),
		BLUE( 4 );

		int bitFlag;

		BitValues( int bitFlag ) {
			this.bitFlag = bitFlag;
		}

		public int getBitFlag() {
			return this.bitFlag;
		}
	}

	private static class TestHibernateBitFlagUserType extends HibernateBitFlag
	{

		public TestHibernateBitFlagUserType() {
			super( BitValues.class );
		}
	}
}
