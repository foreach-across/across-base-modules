package com.foreach.across.modules.hibernate.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

public abstract class HibernateBitFlag implements UserType {

    private final IntegerType TYPE = IntegerType.INSTANCE;
    private final Class clazz;

    public HibernateBitFlag( Class clazz ) {
        this.clazz = clazz;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{TYPE.sqlType()};
    }

    @Override
    public Class returnedClass() {
        return clazz.getClass();
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
    public Object nullSafeGet( ResultSet rs, String[] names, SessionImplementor session, Object owner ) throws HibernateException, SQLException {
        Integer identifier = ( Integer ) TYPE.get( rs, names[0], session );

        if ( rs.wasNull() ) {
            return null;
        }
        return fromInteger( identifier, clazz );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void nullSafeSet( PreparedStatement st, Object value, int index, SessionImplementor session ) throws HibernateException, SQLException {
        try {
            if ( value == null ) {
                st.setNull( index, TYPE.sqlType() );
            } else {
                int result = toInteger( (Set) value );
                TYPE.set( st, result, index, session );
            }
        } catch ( Exception e ) {
            throw new HibernateException( "Exception while getting ids from set", e );
        }
    }

    public <E extends Enum<E> & BitFlag> EnumSet<E> fromInteger( Integer identifier, Class<E> enumType ) {
        EnumSet<E> result = EnumSet.noneOf( enumType );
        E[] enumValues = enumType.getEnumConstants();
        for( E enumValue : enumValues ) {
            if( ( enumValue.getBitFlag() & identifier ) > 0 ) {
                result.add( enumValue );
            }
        }
        return result;
    }

    public <E extends Enum<E> & BitFlag> int toInteger( Set<E> enumSet ) {
        int result = 0;
        for( E enumValue : enumSet ) {
            result |= enumValue.getBitFlag();
        }
        return result;
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
        return ( Serializable ) value;
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
