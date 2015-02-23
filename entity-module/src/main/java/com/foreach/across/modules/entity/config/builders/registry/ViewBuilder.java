package com.foreach.across.modules.entity.config.builders.registry;

import com.foreach.across.modules.entity.config.builders.AbstractEntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.config.builders.AbstractSimpleEntityViewBuilder;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;

public class ViewBuilder extends AbstractSimpleEntityViewBuilder<ConfigurablePropertiesEntityViewFactorySupport, ViewBuilder>
{
	@SuppressWarnings("unchecked")
	public class PropertyRegistryBuilder
			extends EntityViewPropertyRegistryBuilder<PropertyRegistryBuilder>
	{
		public class PropertyDescriptorBuilder extends AbstractEntityPropertyDescriptorBuilder<PropertyDescriptorBuilder>
		{
			@Override
			public PropertyRegistryBuilder and() {
				return propertyRegistryBuilder;
			}
		}

		private final PropertyRegistryBuilder propertyRegistryBuilder;
		private final ViewBuilder parent;

		public PropertyRegistryBuilder( ViewBuilder parent ) {
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
		public ViewBuilder and() {
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
	public EntitiesConfigurationBuilder and() {
		return (EntitiesConfigurationBuilder) parent;
	}
}
