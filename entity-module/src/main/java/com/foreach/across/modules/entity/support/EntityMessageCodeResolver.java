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
package com.foreach.across.modules.entity.support;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.Assert;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import java.util.Locale;

/**
 * Helper for resolving message codes in the context of an EntityConfiguration.
 *
 * @author Arne Vandamme
 */
public class EntityMessageCodeResolver implements MessageSourceAware, MessageCodesResolver
{
	private static class PrefixedEntityMessageCodeResolver extends EntityMessageCodeResolver
	{
		private final EntityMessageCodeResolver parent;

		public PrefixedEntityMessageCodeResolver( EntityMessageCodeResolver parent ) {
			this.parent = parent;
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger( EntityMessageCodeResolver.class );
	private static final Object[] NO_ARGUMENTS = new Object[0];

	private MessageSource messageSource;
	private EntityConfiguration<?> entityConfiguration;

	private MessageCodesResolver errorCodesResolver;

	private String[] prefix = new String[0];
	private String[] fallbackCollections = new String[0];

	public EntityMessageCodeResolver() {
		DefaultMessageCodesResolver errorCodesResolver = new DefaultMessageCodesResolver();
		errorCodesResolver.setPrefix( "validation." );

		this.errorCodesResolver = errorCodesResolver;
	}

	public void setPrefixes( String... prefix ) {
		Assert.notNull( prefix );
		this.prefix = prefix;
	}

	public void setFallbackCollections( String... fallbackCollections ) {
		Assert.notNull( fallbackCollections );
		this.fallbackCollections = fallbackCollections;
	}

	public void setErrorCodesResolver( MessageCodesResolver errorCodesResolver ) {
		Assert.notNull( errorCodesResolver );
		this.errorCodesResolver = errorCodesResolver;
	}

	@Override
	public void setMessageSource( MessageSource messageSource ) {
		this.messageSource = messageSource;
	}

	public void setEntityConfiguration( EntityConfiguration<?> entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
	}

	public String getNameSingular() {
		return getNameSingular( LocaleContextHolder.getLocale() );
	}

	public String getNameSingularInline() {
		return getNameSingularInline( LocaleContextHolder.getLocale() );
	}

	public String getNamePlural() {
		return getNamePlural( LocaleContextHolder.getLocale() );
	}

	public String getNamePluralInline() {
		return getNamePluralInline( LocaleContextHolder.getLocale() );
	}

	public String getNameSingular( Locale locale ) {
		return getMessage( "name.singular", entityConfiguration.getDisplayName(), locale );
	}

	public String getNameSingularInline( Locale locale ) {
		return getMessage( "name.singular.inline", StringUtils.uncapitalize( getNameSingular( locale ) ), locale );
	}

	public String getNamePlural( Locale locale ) {
		return getMessage( "name.plural", getNameSingular( locale ) + "s", locale );
	}

	public String getNamePluralInline( Locale locale ) {
		return getMessage( "name.plural.inline", StringUtils.uncapitalize( getNamePlural( locale ) ), locale );
	}

	public String getPropertyDisplayName( EntityPropertyDescriptor descriptor ) {
		return getPropertyDisplayName( descriptor, LocaleContextHolder.getLocale() );
	}

	public String getPropertyDescription( EntityPropertyDescriptor descriptor ) {
		return getPropertyDescription( descriptor, LocaleContextHolder.getLocale() );
	}

	public String getPropertyMessage( EntityPropertyDescriptor descriptor, String subKey, String defaultValue ) {
		return getPropertyMessage( descriptor, subKey, defaultValue, LocaleContextHolder.getLocale() );
	}

	public String getPropertyDisplayName( EntityPropertyDescriptor descriptor, Locale locale ) {
		return getPropertyMessage( descriptor, null, descriptor.getDisplayName(), locale );
	}

	public String getPropertyDescription( EntityPropertyDescriptor descriptor, Locale locale ) {
		return getPropertyMessage( descriptor, "description", "", locale );
	}

	public String getPropertyMessage( EntityPropertyDescriptor descriptor,
	                                  String subKey,
	                                  String defaultValue,
	                                  Locale locale ) {
		String code = "properties." + descriptor.getName() + ( subKey != null ? "[" + subKey + "]" : "" );
		return getMessageWithFallback( code, new Object[0], defaultValue, locale );
	}

	public String getMessage( String code, String defaultValue ) {
		return getMessage( code, NO_ARGUMENTS, defaultValue );
	}

	public String getMessage( String code, String defaultValue, Locale locale ) {
		return getMessage( code, NO_ARGUMENTS, defaultValue, locale );
	}

	public String getMessage( String code, Object[] arguments, String defaultValue ) {
		return getMessage( code, arguments, defaultValue, LocaleContextHolder.getLocale() );
	}

	public String getMessage( String code, Object[] arguments, String defaultValue, Locale locale ) {
		return messageSource.getMessage(
				buildMessageSourceResolvable( prefix, code, arguments, defaultValue ),
				locale
		);
	}

	public String getMessageWithFallback( String code, String defaultValue ) {
		return getMessageWithFallback( code, NO_ARGUMENTS, defaultValue );
	}

	public String getMessageWithFallback( String code, String defaultValue, Locale locale ) {
		return getMessageWithFallback( code, NO_ARGUMENTS, defaultValue, locale );
	}

	public String getMessageWithFallback( String code, Object[] arguments, String defaultValue ) {
		return getMessageWithFallback( code, arguments, defaultValue, LocaleContextHolder.getLocale() );
	}

	public String getMessageWithFallback( String code, Object[] arguments, String defaultValue, Locale locale ) {
		return messageSource.getMessage(
				buildMessageSourceResolvable(
						ArrayUtils.addAll( prefix, fallbackCollections ),
						code, arguments, defaultValue
				),
				locale
		);
	}

	@Override
	public String[] resolveMessageCodes( String errorCode, String objectName ) {
		return addPrefixesToMessageCodes( errorCodesResolver.resolveMessageCodes( errorCode, objectName ), true );
	}

	@Override
	public String[] resolveMessageCodes( String errorCode, String objectName, String field, Class<?> fieldType ) {
		return addPrefixesToMessageCodes(
				errorCodesResolver.resolveMessageCodes( errorCode, objectName, field, fieldType ), true
		);
	}

	public String[] addPrefixesToMessageCodes( String[] codes, boolean includeFallback ) {
		return includeFallback
				? addPrefix( ArrayUtils.addAll( prefix, fallbackCollections ), codes )
				: addPrefix( prefix, codes );
	}

	public EntityMessageCodeResolver prefixedResolver( String... additionalPrefixes ) {
		return prefixedResolver( true, additionalPrefixes );
	}

	public EntityMessageCodeResolver prefixedResolver( boolean keepCurrentPrefix, String... additionalPrefixes ) {
		String[] newPrefixes = keepCurrentPrefix
				? ensureCurrentPrefixesRemain( additionalPrefixes ) : additionalPrefixes;

		PrefixedEntityMessageCodeResolver resolver = new PrefixedEntityMessageCodeResolver( this );
		resolver.setMessageSource( messageSource );
		resolver.setEntityConfiguration( entityConfiguration );
		resolver.setPrefixes( addPrefix( newPrefixes, prefix ) );
		resolver.setFallbackCollections( addPrefix( newPrefixes, fallbackCollections ) );

		return resolver;
	}

	private String[] ensureCurrentPrefixesRemain( String[] additionalPrefixes ) {
		if ( !ArrayUtils.contains( additionalPrefixes, "" ) ) {
			return ArrayUtils.add( additionalPrefixes, "" );
		}

		return additionalPrefixes;
	}

	private String[] addPrefix( String[] prefixes, String[] names ) {
		String[] prefixed = new String[prefixes.length * names.length];

		if ( prefixes.length == 0 ) {
			return names;
		}

		if ( names.length == 0 ) {
			return prefixes;
		}

		int ix = 0;
		for ( String prefix : prefixes ) {
			for ( String name : names ) {
				if ( StringUtils.isBlank( prefix ) ) {
					prefixed[ix++] = name;
				}
				else if ( StringUtils.isBlank( name ) ) {
					prefixed[ix++] = prefix;
				}
				else {
					prefixed[ix++] = prefix + "." + name;
				}
			}
		}

		return prefixed;
	}

	protected MessageSourceResolvable buildMessageSourceResolvable( String[] collections,
	                                                                String code,
	                                                                Object[] arguments,
	                                                                String defaultValue ) {
		String[] codes = generateCodes( collections, new String[0], code );

		if ( LOG.isTraceEnabled() ) {
			LOG.trace( "Looking up {}", StringUtils.join( codes, ", " ) );
		}

		return new DefaultMessageSourceResolvable( codes, arguments,
		                                           defaultValue != null ? defaultValue : codes[0] );
	}

	public static String[] generateCodes( String[] rootCollections, String[] subCollections, String itemKey ) {
		Assert.notNull( rootCollections );
		Assert.notNull( subCollections );
		Assert.notNull( itemKey );

		if ( rootCollections.length == 0 && subCollections.length == 0 ) {
			return new String[] { itemKey };
		}

		int index = 0;

		String[] codes;

		if ( rootCollections.length == 0 ) {
			codes = new String[subCollections.length + 1];

			for ( String subCollection : subCollections ) {
				codes[index++] = subCollection + "." + itemKey;
			}
			codes[index] = itemKey;
		}
		else {
			codes = new String[( subCollections.length + 1 ) * rootCollections.length];

			for ( String rootCollection : rootCollections ) {
				String prefix = StringUtils.isEmpty( rootCollection ) ? "" : rootCollection + ".";
				for ( String subCollection : subCollections ) {
					codes[index++] = prefix + subCollection + "." + itemKey;
				}
				codes[index++] = prefix + itemKey;
			}
		}

		return codes;
	}
}
