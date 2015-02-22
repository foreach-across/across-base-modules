package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;

public class EntityAssociationBuilder extends EntityBuilderSupport<EntityAssociationBuilder, MutableEntityAssociation>
{
	public class ViewBuilder extends SimpleEntityViewBuilder<ConfigurablePropertiesEntityViewFactorySupport, ViewBuilder>
	{
		public class Properties extends EntityViewPropertyRegistryBuilder<Properties>
		{
			@Override
			public ViewBuilder and() {
				return viewBuilder;
			}
		}

		private final ViewBuilder viewBuilder;

		public ViewBuilder() {
			this.viewBuilder = this;
		}

		@Override
		public Properties properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		public Properties properties() {
			return (Properties) super.properties();
		}

		@Override
		protected Properties createPropertiesBuilder() {
			return new Properties();
		}

		@Override
		public EntityAssociationBuilder and() {
			return self;
		}
	}

	public class ListViewBuilder extends EntityListViewBuilder<ListViewBuilder>
	{
		public class Properties extends EntityViewPropertyRegistryBuilder<Properties>
		{
			@Override
			public ListViewBuilder and() {
				return viewBuilder;
			}
		}

		private final ListViewBuilder viewBuilder;

		public ListViewBuilder() {
			this.viewBuilder = this;
		}

		@Override
		public Properties properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		public Properties properties() {
			return (Properties) super.properties();
		}

		@Override
		protected Properties createPropertiesBuilder() {
			return new Properties();
		}

		@Override
		public EntityAssociationBuilder and() {
			return self;
		}
	}

	public class FormViewBuilder extends EntityFormViewBuilder<FormViewBuilder>
	{
		public class Properties extends EntityViewPropertyRegistryBuilder<Properties>
		{
			@Override
			public FormViewBuilder and() {
				return viewBuilder;
			}
		}

		private final FormViewBuilder viewBuilder;

		public FormViewBuilder() {
			this.viewBuilder = this;
		}

		@Override
		public Properties properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		public Properties properties() {
			return (Properties) super.properties();
		}

		@Override
		protected Properties createPropertiesBuilder() {
			return new Properties();
		}

		@Override
		public EntityAssociationBuilder and() {
			return self;
		}
	}

	private final String name;
	private final EntityAssociationBuilder self;

	public EntityAssociationBuilder( String name ) {
		this.self = this;
		this.name = name;
	}

	@Override
	public ViewBuilder view( String name ) {
		return view( name, ViewBuilder.class );
	}

	@Override
	public ListViewBuilder listView() {
		return listView( EntityListView.VIEW_NAME );
	}

	@Override
	public ListViewBuilder listView( String name ) {
		return view( name, ListViewBuilder.class );
	}

	@Override
	public FormViewBuilder createFormView() {
		return formView( EntityFormView.CREATE_VIEW_NAME );
	}

	@Override
	public FormViewBuilder updateFormView() {
		return formView( EntityFormView.UPDATE_VIEW_NAME );
	}

	@Override
	public FormViewBuilder formView( String name ) {
		return view( name, FormViewBuilder.class );
	}

	void apply( MutableEntityConfiguration configuration, MutableEntityRegistry entityRegistry ) {
		MutableEntityAssociation association = configuration.createAssociation( name );

		// todo: set source, target etc

		applyAttributes( association );
		applyViewBuilders( association );
	}

	public void postProcess( MutableEntityConfiguration configuration ) {
		MutableEntityAssociation association = configuration.association( name );

		for ( PostProcessor<MutableEntityAssociation> postProcessor : postProcessors() ) {
			postProcessor.process( association );
		}
	}
}
