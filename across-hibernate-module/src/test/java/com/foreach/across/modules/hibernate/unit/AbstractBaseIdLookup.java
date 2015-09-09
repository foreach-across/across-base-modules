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

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.usertype.UserType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public abstract class AbstractBaseIdLookup
{
	protected Object mockResultSetAndTestValue( UserType userType, Integer rowValue ) throws SQLException {
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


	protected void mockResultSetAndTestValueToInt( UserType userType, Integer expectedValue,
	                                             Object rowValue ) throws SQLException {
		PreparedStatement preparedStatement = mock( PreparedStatement.class );
		SessionImplementor sessionImplementor = mock( SessionImplementor.class );
		SessionFactoryImplementor sessionFactoryImplementor = mock( SessionFactoryImplementor.class );
		when( sessionImplementor.getFactory() ).thenReturn( sessionFactoryImplementor );
		when( sessionFactoryImplementor.getDialect() ).thenReturn( new HSQLDialect() );

		userType.nullSafeSet( preparedStatement, rowValue, 3, sessionImplementor );

		if( expectedValue == null ) {
			verify( preparedStatement, times( 1 ) ).setNull( eq( 3 ), eq( IntegerType.INSTANCE.sqlType() ) );
		} else {
			verify( preparedStatement, times( 1 ) ).setInt( eq( 3 ), eq( expectedValue ) );
		}

	}
}
