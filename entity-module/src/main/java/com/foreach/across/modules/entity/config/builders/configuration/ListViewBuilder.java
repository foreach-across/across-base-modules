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
package com.foreach.across.modules.entity.config.builders.configuration;

import com.foreach.across.modules.entity.config.builders.AbstractEntityListViewBuilder;
import com.foreach.across.modules.entity.config.builders.AbstractEntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.views.support.ValueFetcher;

public class ListViewBuilder extends AbstractEntityListViewBuilder<ListViewBuilder>
{
	@SuppressWarnings("unchecked")
	public class PropertyRegistryBuilder
			extends EntityViewPropertyRegistryBuilder<PropertyRegistryBuilder>
	{
		public class PropertyDescriptorBuilder extends AbstractEntityPropertyDescriptorBuilder<PropertyDescriptorBuilder>
		{
			@Override
			public PropertyDescriptorBuilder attribute( String name, Object value ) {
				return super.attribute( name, value );
			}

			@Override
			public <S> PropertyDescriptorBuilder attribute( Class<S> type, S value ) {
				return super.attribute( type, value );
			}

			@Override
			public PropertyDescriptorBuilder displayName( String displayName ) {
				return super.displayName( displayName );
			}

			@Override
			public PropertyDescriptorBuilder spelValueFetcher( String expression ) {
				return super.spelValueFetcher( expression );
			}

			@Override
			public PropertyDescriptorBuilder valueFetcher( ValueFetcher valueFetcher ) {
				return super.valueFetcher( valueFetcher );
			}

			@Override
			public PropertyRegistryBuilder and() {
				return propertyRegistryBuilder;
			}
		}

		private final PropertyRegistryBuilder propertyRegistryBuilder;
		private final ListViewBuilder parent;

		public PropertyRegistryBuilder( ListViewBuilder parent ) {
			this.propertyRegistryBuilder = this;
			this.parent = parent;
		}

		@Override
		public synchronized PropertyDescriptorBuilder property( String name ) {
			return (PropertyDescriptorBuilder) super.property( name );
		}

		@Override
		protected PropertyDescriptorBuilder createDescriptorBuilder( String name ) {
			return new PropertyDescriptorBuilder();
		}

		@Override
		public ListViewBuilder and() {
			return parent;
		}
	}

	@Override
	public PropertyRegistryBuilder properties() {
		return (PropertyRegistryBuilder) super.properties();
	}

	@Override
	public PropertyRegistryBuilder properties( String... propertyNames ) {
		return (PropertyRegistryBuilder) super.properties( propertyNames );
	}

	@Override
	protected PropertyRegistryBuilder createPropertiesBuilder() {
		return new PropertyRegistryBuilder( this );
	}

	@Override
	public EntityConfigurationBuilder and() {
		return (EntityConfigurationBuilder) parent;
	}
}
