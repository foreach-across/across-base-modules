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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertyRegistryBuilder<SELF extends AbstractEntityPropertyRegistryBuilder>
{
	private final Map<String, AbstractEntityPropertyDescriptorBuilder> builders = new HashMap<>();

	@SuppressWarnings( "unchecked" )
	public synchronized AbstractEntityPropertyDescriptorBuilder property( String name ) {
		Assert.notNull( name );

		AbstractEntityPropertyDescriptorBuilder builder = builders.get( name );

		if ( builder == null ) {
			builder = createDescriptorBuilder( name );
			builder.setName( name );
			builders.put( name, builder );
		}

		return builder;
	}

	protected abstract AbstractEntityPropertyDescriptorBuilder createDescriptorBuilder( String name );

	/**
	 * @return parent builder
	 */
	public abstract Object and();

	protected void apply( EntityPropertyRegistry entityPropertyRegistry ) {
		for ( AbstractEntityPropertyDescriptorBuilder builder : builders.values() ) {
			builder.apply( entityPropertyRegistry );
		}
	}
}
