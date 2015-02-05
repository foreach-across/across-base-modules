package com.foreach.across.modules.entity.util;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;

public class EntityUtils
{
	public static String generateDisplayName( String propertyName ) {
		String cleaned = propertyName.replace( '.', ' ' ).replace( '_', ' ' ).replaceAll( "[^\\p{L}\\p{Nd} ]+", "" );

		List<String> finished = new LinkedList<>();
		for ( String part : StringUtils.split( cleaned, ' ' ) ) {
			String capitalized = StringUtils.isAllUpperCase( part )
					? StringUtils.capitalize( StringUtils.lowerCase( part ) ) : StringUtils.capitalize( part );

			for ( String subPart : StringUtils.splitByCharacterTypeCamelCase( capitalized ) ) {
				finished.add( finished.isEmpty() ? subPart : StringUtils.lowerCase( subPart ) );
			}
		}

		return StringUtils.join( finished, " " );
	}

	public static Object getPropertyValue( PropertyDescriptor descriptor, Object instance ) {
		try {
			return descriptor.getReadMethod().invoke( instance );
		}
		catch ( Exception e ) {
			return null;
		}
	}

	public static String combineDisplayNames( String first, String... propertyNames ) {
		List<String> finished = new LinkedList<>();
		finished.add( generateDisplayName( first ) );
		for ( String name : propertyNames ) {
			finished.add( generateDisplayName( name ).toLowerCase() );
		}
		return StringUtils.join( finished, " " );
	}
}
