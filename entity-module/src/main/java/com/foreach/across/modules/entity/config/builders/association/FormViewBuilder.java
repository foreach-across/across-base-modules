package com.foreach.across.modules.entity.config.builders.association;

import com.foreach.across.modules.entity.config.builders.AbstractEntityFormViewBuilder;
import com.foreach.across.modules.entity.config.builders.AbstractEntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;

public class FormViewBuilder extends AbstractEntityFormViewBuilder<FormViewBuilder>
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
		private final FormViewBuilder parent;

		public PropertyRegistryBuilder( FormViewBuilder parent ) {
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
		public FormViewBuilder and() {
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
	public EntityAssociationBuilder and() {
		return (EntityAssociationBuilder) parent;
	}
}
