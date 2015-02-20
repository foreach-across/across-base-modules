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
package com.foreach.across.modules.entity.formatters;

import liquibase.exception.DateParseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.Formatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Andy Somers
 */
public class DateFormatter extends org.springframework.format.datetime.DateFormatter
{
	private static Set<DateFormat> dateFormats = new HashSet<>();

	static {
		dateFormats.add( new SimpleDateFormat( "yyyy-MM-dd" ) );
		dateFormats.add( new SimpleDateFormat( "yyyy/MM/dd" ) );
		dateFormats.add( new SimpleDateFormat( "yyyy-MM-dd HH:mm" ) );
		dateFormats.add( new SimpleDateFormat( "yyyy/MM/dd HH:mm" ) );
		dateFormats.add( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
		dateFormats.add( new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" ) );
	}

	@Override
	public Date parse( String text, Locale locale ) throws ParseException {
		if ( StringUtils.isNotBlank( text ) ) {
			DateFormat instance = DateFormat.getDateInstance( DateFormat.SHORT, locale );
			Set<DateFormat> availableDateFormats = new LinkedHashSet<>();
			availableDateFormats.add( instance );
			availableDateFormats.addAll( dateFormats );
			return parseDate( text, availableDateFormats );
		}
		return null;
	}

	@Override
	public String print( Date object, Locale locale ) {
		if ( object != null ) {
			DateFormat instance = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM, locale );
			return instance.format( object );
		}
		return null;
	}

	private Date parseDate( String text, Set<DateFormat> availableDateFormats ) throws DateParseException {
		for ( DateFormat format : availableDateFormats ) {
			try {
				return format.parse( text );
			}
			catch ( ParseException e ) {
				// do nothing
			}
		}
		throw new DateParseException( "Unable to parse date" );
	}
}
