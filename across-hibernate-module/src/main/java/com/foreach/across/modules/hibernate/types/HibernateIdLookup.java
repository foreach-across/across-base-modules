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

import com.foreach.across.modules.hibernate.util.HibernateTypeLookup;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Defines a generic strategy to transform enum values and their representation in the database.
 * Tries to match a hibernate {@link org.hibernate.type.Type} to the Id class as specified on the actual IdLookup implementation.
 *
 * @param <K>
 * @param <T>
 */
public abstract class HibernateIdLookup<T extends IdLookup<K>, K> implements UserType
{
	private final AbstractSingleColumnStandardBasicType<K> type;
	private final Class<T> clazz;

	public HibernateIdLookup( Class<T> clazz ) {
		this.type = HibernateTypeLookup.getForIdLookupType( clazz );
		this.clazz = clazz;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { type.sqlType() };
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
	@SuppressWarnings("unchecked")
	public Object nullSafeGet( ResultSet rs,
	                           String[] names,
	                           SharedSessionContractImplementor session,
	                           Object owner ) throws HibernateException, SQLException {
		K identifier = (K) type.get( rs, names[0], session );

		if ( identifier == null ) {
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
	public void nullSafeSet( PreparedStatement st,
	                         Object value,
	                         int index,
	                         SharedSessionContractImplementor session ) throws HibernateException, SQLException {
		try {
			if ( value != null ) {
				type.set( st, ( (IdLookup<K>) value ).getId(), index, session );
			}
			else {
				st.setNull( index, type.sqlType() );
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
