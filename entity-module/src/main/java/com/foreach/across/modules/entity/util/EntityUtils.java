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
package com.foreach.across.modules.entity.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EntityUtils
{
	public static String generateDisplayName( String propertyName ) {
		String cleaned = propertyName.replace( '.', ' ' ).replace( '_', ' ' ).replaceAll( "[^\\p{L}\\p{Nd} ]+", " " );

		List<String> finished = new LinkedList<>();
		for ( String part : StringUtils.split( cleaned, ' ' ) ) {
			String previous = null;
			String capitalized = StringUtils.isAllUpperCase( part )
					? StringUtils.capitalize( StringUtils.lowerCase( part ) ) : StringUtils.capitalize( part );

			for ( String subPart : StringUtils.splitByCharacterTypeCamelCase( capitalized ) ) {
				if ( previous != null && ( StringUtils.length( previous ) == 1 || StringUtils.isNumeric( subPart ) ) ) {
					previous = previous + subPart;
					finished.set( finished.size() - 1, previous );
				}
				else {
					previous = finished.isEmpty() ? subPart : StringUtils.lowerCase( subPart );
					finished.add( previous );
				}
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

	/**
	 * Create a {@link org.springframework.data.domain.Page} from any {@link java.lang.Iterable}.
	 *
	 * @param collection contains the items in the page
	 * @return Page instance
	 */
	public static Page<?> createPage( Iterable<?> collection ) {
		if ( collection instanceof List ) {
			return new PageImpl<>( (List<?>) collection );
		}

		if ( collection instanceof Collection ) {
			return new PageImpl<>( new ArrayList<>( (Collection) collection ) );
		}

		List<Object> list = new ArrayList<>();
		for ( Object item : collection ) {
			list.add( item );
		}

		return new PageImpl<>( list );
	}
}
