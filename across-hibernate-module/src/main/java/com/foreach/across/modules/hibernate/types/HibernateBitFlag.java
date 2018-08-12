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
package com.foreach.across.modules.hibernate.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.usertype.UserType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

public abstract class HibernateBitFlag<T extends BitFlag> extends HibernateIdLookup<T, Integer> implements UserType
{
	private final IntegerType TYPE = IntegerType.INSTANCE;

	public HibernateBitFlag( Class<T> clazz ) {
		super( clazz );
	}

	@Override
	public Object nullSafeGet( ResultSet rs,
	                           String[] names,
	                           SharedSessionContractImplementor session,
	                           Object owner ) throws HibernateException, SQLException {
		Integer identifier = (Integer) TYPE.get( rs, names[0], session );

		if ( rs.wasNull() ) {
			return EnumSet.noneOf( super.returnedClass() );
		}
		return fromInteger( identifier, super.returnedClass() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public void nullSafeSet( PreparedStatement st,
	                         Object value,
	                         int index,
	                         SharedSessionContractImplementor session ) throws HibernateException, SQLException {
		try {
			int result = toInteger( (Set) value );
			TYPE.set( st, result, index, session );
		}
		catch ( Exception e ) {
			throw new HibernateException( "Exception while getting ids from set", e );
		}
	}

	private <E extends Enum<E> & BitFlag> EnumSet<E> fromInteger( Integer identifier, Class<E> enumType ) {
		EnumSet<E> result = EnumSet.noneOf( enumType );
		E[] enumValues = enumType.getEnumConstants();
		for ( E enumValue : enumValues ) {
			if ( ( enumValue.getId() & identifier ) > 0 ) {
				result.add( enumValue );
			}
		}
		return result;
	}

	private <E extends Enum<E> & BitFlag> int toInteger( Set<E> enumSet ) {
		int result = 0;
		if ( enumSet != null ) {
			for ( E enumValue : enumSet ) {
				result |= enumValue.getId();
			}
		}
		return result;
	}
}
