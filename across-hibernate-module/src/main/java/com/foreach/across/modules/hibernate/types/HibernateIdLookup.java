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
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class HibernateIdLookup<T extends IdLookup> implements UserType
{
	private final IntegerType TYPE = IntegerType.INSTANCE;
	private final Class<T> clazz;

	public HibernateIdLookup( Class<T> clazz ) {
		this.clazz = clazz;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { TYPE.sqlType() };
	}

	@Override
	public Class returnedClass() {
		return clazz;
	}

	@Override
	public boolean equals( Object x, Object y ) throws HibernateException {
		return x == y;
	}

	@Override
	public int hashCode( Object x ) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet( ResultSet rs,
	                           String[] names,
	                           SessionImplementor session,
	                           Object owner ) throws HibernateException, SQLException {
		Integer identifier = (Integer) TYPE.get( rs, names[0], session );

		if( identifier == null ) {
			return null;
		}
		T[] enumValues = clazz.getEnumConstants();
		for ( T enumValue : enumValues ) {
			if ( enumValue.getId().equals( identifier ) ) {
				return enumValue;
			}
		}
		throw new HibernateException( "Could not find enum for value: " + identifier + " in clazz " + clazz );
	}

	@Override
	@SuppressWarnings("unchecked")
	public void nullSafeSet( PreparedStatement st,
	                         Object value,
	                         int index,
	                         SessionImplementor session ) throws HibernateException, SQLException {
		try {
			if( value != null ) {
				TYPE.set( st, ((IdLookup<Integer>) value).getId(), index, session );
			} else {
				st.setNull( index, TYPE.sqlType() );
			}
		}
		catch ( Exception e ) {
			throw new HibernateException( "Exception while getting ids from set", e );
		}
	}

	@Override
	public Object deepCopy( Object value ) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble( Object value ) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble( Serializable cached, Object owner ) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace( Object original, Object target, Object owner ) throws HibernateException {
		return original;
	}
}
