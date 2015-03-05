package com.foreach.across.modules.hibernate.util;

import com.foreach.across.modules.hibernate.types.IdLookup;
import org.hibernate.type.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;

/**
 * @author niels
 * @since 5/03/2015
 */
public class HibernateTypeLookup
{
	private static final Map<Class<?>, AbstractSingleColumnStandardBasicType> typeForClass = new HashMap<>();

	static {
		// support a basic subset of available types in hibernate, because for now,
		// we do not want to make a distinction between different types based on metadata of the actual field
		typeForClass.put( BigDecimal.class, BigDecimalType.INSTANCE );
		typeForClass.put( BigInteger.class, BigIntegerType.INSTANCE );
		typeForClass.put( Byte.class, ByteType.INSTANCE );
		typeForClass.put( byte[].class, BinaryType.INSTANCE );
		typeForClass.put( Byte[].class, WrapperBinaryType.INSTANCE );
		typeForClass.put( char[].class, CharArrayType.INSTANCE );
		typeForClass.put( Character.class, CharacterType.INSTANCE );
		typeForClass.put( Character[].class, CharacterArrayType.INSTANCE );
		typeForClass.put( Class.class, ClassType.INSTANCE );
		typeForClass.put( Currency.class, CurrencyType.INSTANCE );
		typeForClass.put( Date.class, DateType.INSTANCE );
		typeForClass.put( Double.class, DoubleType.INSTANCE );
		typeForClass.put( Float.class, FloatType.INSTANCE );
		typeForClass.put( Integer.class, IntegerType.INSTANCE );
		typeForClass.put( Locale.class, LocaleType.INSTANCE );
		typeForClass.put( Long.class, LongType.INSTANCE );
		typeForClass.put( Short.class, ShortType.INSTANCE );
		typeForClass.put( String.class, StringType.INSTANCE );
		typeForClass.put( URL.class, UrlType.INSTANCE );
		typeForClass.put( UUID.class, UUIDBinaryType.INSTANCE );
	}

	/**
	 * Retrieves the hibernate Type instance that should be used to convert the database value into an object and the reverse operation.
	 * This method recursively descends into the class hierarchy until it finds the actual type information and throws an error if it can't find the information
	 * or if the specified type is not currently supported.
	 *
	 * @param clazz The type that implements the IdLookup interface
	 * @param <K>   The type of id used to designate an enum value in the database
	 * @return the retrieved hibernate type implementation to perform the conversions
	 * @throws java.lang.IllegalArgumentException
	 * @see org.hibernate.type.AbstractSingleColumnStandardBasicType
	 */
	@SuppressWarnings("unchecked")
	public static <K> AbstractSingleColumnStandardBasicType<K> getForIdLookupType( Class<? extends IdLookup<K>> clazz ) {
		Type[] genericInterfaces = clazz.getGenericInterfaces();
		Class<K> idClazz = null;
		for ( Type genericInterface : genericInterfaces ) {
			if ( genericInterface instanceof ParameterizedType ) {
				ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
				if ( IdLookup.class.isAssignableFrom( ( (Class) parameterizedType.getRawType() ) ) ) {
					idClazz = (Class<K>) parameterizedType.getActualTypeArguments()[0];
					break;
				}
			}
			else if ( IdLookup.class.isAssignableFrom( (Class) genericInterface ) ) {
				return getForIdLookupType( (Class) genericInterface );
			}
		}
		if ( idClazz == null ) {
			// in theory this can never happen, as the signature of the parameter restricts us to IdLookup implementations
			throw new IllegalArgumentException( "You should implement IdLookup interface" );
		}

		if ( typeForClass.containsKey( idClazz ) ) {
			return typeForClass.get( idClazz );
		}
		throw new IllegalArgumentException( "Class " + idClazz.getName() + " is not supported for id lookup" );
	}
}
