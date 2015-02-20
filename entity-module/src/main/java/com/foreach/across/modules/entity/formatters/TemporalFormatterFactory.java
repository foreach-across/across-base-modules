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
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A FormatterFactory for fields that have a {@link javax.persistence.Temporal} annotation.
 *
 * @author Andy Somers
 */
public class TemporalFormatterFactory implements AnnotationFormatterFactory<Temporal>
{
	@Override
	public Set<Class<?>> getFieldTypes() {
		return new HashSet<>( Arrays.asList( new Class<?>[] { Date.class } ) );
	}

	@Override
	public Printer<?> getPrinter( Temporal annotation, Class<?> fieldType ) {
		return new DateWithTemporalAnnotationFormatter( annotation.value() );
	}

	@Override
	public Parser<?> getParser( Temporal annotation, Class<?> fieldType ) {
		return new DateWithTemporalAnnotationFormatter( annotation.value() );
	}

	private static class DateWithTemporalAnnotationFormatter implements org.springframework.format.Formatter<Date>
	{
		private static Set<DateFormat> dateFormats = new HashSet<>();
		private static Set<DateFormat> timeFormats = new HashSet<>();
		private static Set<DateFormat> dateTimeFormats = new HashSet<>();

		static {
			dateFormats.add( new SimpleDateFormat( "yyyy-MM-dd" ) );
			dateFormats.add( new SimpleDateFormat( "yyyy/MM/dd" ) );
			dateTimeFormats.add( new SimpleDateFormat( "yyyy-MM-dd HH:mm" ) );
			dateTimeFormats.add( new SimpleDateFormat( "yyyy/MM/dd HH:mm" ) );
			dateTimeFormats.add( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
			dateTimeFormats.add( new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" ) );
			timeFormats.add( new SimpleDateFormat( "HH:mm" ) );
			timeFormats.add( new SimpleDateFormat( "HH:mm" ) );
			timeFormats.add( new SimpleDateFormat( "HH:mm:ss" ) );
			timeFormats.add( new SimpleDateFormat( "HH:mm:ss" ) );
		}

		private TemporalType temporalType;

		public DateWithTemporalAnnotationFormatter( TemporalType temporalType ) {
			this.temporalType = temporalType;
		}

		@Override
		public Date parse( String text, Locale locale ) throws ParseException {
			if ( StringUtils.isNotBlank( text ) ) {
				DateFormat instance;
				Set<DateFormat> defaultDateFormats;
				switch ( temporalType ) {
					case TIME:
						instance = DateFormat.getTimeInstance( DateFormat.SHORT, locale );
						defaultDateFormats = timeFormats;
						break;
					case DATE:
						instance = DateFormat.getDateInstance( DateFormat.SHORT, locale );
						defaultDateFormats = dateFormats;
						break;
					case TIMESTAMP:
					default:
						instance = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, locale );
						defaultDateFormats = dateTimeFormats;
						break;
				}
				Set<DateFormat> availableDateFormats = new LinkedHashSet<>();
				availableDateFormats.add( instance );
				availableDateFormats.addAll( defaultDateFormats );
				return parseDate( text, availableDateFormats );
			}
			throw new DateParseException( "Unable to parse date" );
		}

		@Override
		public String print( Date object, Locale locale ) {
			if ( object != null ) {
				DateFormat instance;
				switch ( temporalType ) {
					case TIMESTAMP:
						instance = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM, locale );
						break;
					case TIME:
						instance = DateFormat.getTimeInstance( DateFormat.SHORT, locale );
						break;
					case DATE:
					default:
						instance = DateFormat.getDateInstance( DateFormat.SHORT, locale );
						break;
				}
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
}
