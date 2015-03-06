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
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AbstractEntityPropertyDescriptorBuilder<SELF extends AbstractEntityPropertyDescriptorBuilder>
{
	private String name;

	private String displayName;
	private ValueFetcher valueFetcher;

	private Boolean hidden, writable, readable;

	private final Map<Object, Object> attributes = new HashMap<>();

	protected void setName( String name ) {
		this.name = name;
	}

	/**
	 * Add a custom attribute this builder should add to the property descriptor.
	 *
	 * @param name  Name of the attribute.
	 * @param value Value of the attribute.
	 * @return current builder
	 */
	public SELF attribute( String name, Object value ) {
		Assert.notNull( name );
		attributes.put( name, value );
		return (SELF) this;
	}

	/**
	 * Add a custom attribute this builder should add to the property descriptor.
	 *
	 * @param type  Type of the attribute.
	 * @param value Value of the attribute.
	 * @param <S>   Class that is both key and value type of the attribute
	 * @return current builder
	 */
	public <S> SELF attribute( Class<S> type, S value ) {
		Assert.notNull( type );
		attributes.put( type, value );
		return (SELF) this;
	}

	/**
	 * @param displayName Display name to be configured on the property.
	 * @return current builder
	 */
	public SELF displayName( String displayName ) {
		this.displayName = displayName;
		return (SELF) this;
	}

	/**
	 * @param expression SpEL expression that should be used as value.
	 * @return current builder
	 */
	public SELF spelValueFetcher( String expression ) {
		Assert.notNull( expression );
		return valueFetcher( new SpelValueFetcher( expression ) );
	}

	/**
	 * @param valueFetcher fetcher to configure on the property
	 * @return current builder
	 */
	public SELF valueFetcher( ValueFetcher valueFetcher ) {
		this.valueFetcher = valueFetcher;
		return (SELF) this;
	}

	/**
	 * @param writable true if the property should be writable in a UI
	 * @return current builder
	 */
	public SELF writable( boolean writable ) {
		this.writable = writable;
		return (SELF) this;
	}

	/**
	 * @param readable true if the property should be viewable/readable in a UI
	 * @return current builder
	 */
	public SELF readable( boolean readable ) {
		this.readable = readable;
		return (SELF) this;
	}

	/**
	 * @param hidden true if the property should be hidden from a UI
	 * @return current builder
	 */
	public SELF hidden( boolean hidden ) {
		this.hidden = hidden;
		return (SELF) this;
	}

	/**
	 * @return parent builder
	 */
	public abstract Object and();

	void apply( EntityPropertyRegistry entityPropertyRegistry ) {
		EntityPropertyDescriptor existing = entityPropertyRegistry.getProperty( name );
		MutableEntityPropertyDescriptor descriptor;

		// Modify the existing if possible, or create a new one
		if ( existing instanceof MutableEntityPropertyDescriptor ) {
			descriptor = (MutableEntityPropertyDescriptor) existing;
		}

		else {
			descriptor = new SimpleEntityPropertyDescriptor( name );
		}

		// Update configured properties
		if ( displayName != null ) {
			descriptor.setDisplayName( displayName );
		}

		if ( valueFetcher != null ) {
			descriptor.setValueFetcher( valueFetcher );
			descriptor.setReadable( true );
		}
		else if ( existing == null ) {
			descriptor.setValueFetcher( new SpelValueFetcher( name ) );
			descriptor.setReadable( true );
		}

		if ( writable != null ) {
			descriptor.setWritable( writable );
		}

		if ( readable != null ) {
			descriptor.setReadable( readable );
		}

		if ( hidden != null ) {
			descriptor.setHidden( hidden );
		}

		descriptor.addAllAttributes( attributes );

		// There was an existing descriptor, but not mutable, we created a custom merge
		if ( existing != null && !( existing instanceof MutableEntityPropertyDescriptor ) ) {
			descriptor = (MutableEntityPropertyDescriptor) existing.merge( descriptor );
		}

		entityPropertyRegistry.register( descriptor );
	}
}
