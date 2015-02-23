package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.config.builders.association.FormViewBuilder;
import com.foreach.across.modules.entity.config.builders.association.ListViewBuilder;
import com.foreach.across.modules.entity.config.builders.association.ViewBuilder;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;

public class EntityAssociationBuilder extends AbstractAttributesAndViewsBuilder<EntityAssociationBuilder, MutableEntityAssociation>
{
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
