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

import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.config.builders.association.FormViewBuilder;
import com.foreach.across.modules.entity.config.builders.association.ListViewBuilder;
import com.foreach.across.modules.entity.config.builders.association.ViewBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import org.springframework.util.Assert;

public class EntityAssociationBuilder extends AbstractAttributesAndViewsBuilder<EntityAssociationBuilder, MutableEntityAssociation>
{
	private final String name;

	private Class<?> targetEntityType;
	private String sourceProperty, targetProperty;

	private boolean sourcePropertyRemoved, targetPropertyRemoved;

	public EntityAssociationBuilder( String name ) {
		this.name = name;
	}

	/**
	 * Set the target entity type for this association.  This type must be registered in the
	 * {@link com.foreach.across.modules.entity.registry.EntityRegistry}.
	 *
	 * @param targetEntityType type of the target entity
	 * @return current builder
	 */
	public EntityAssociationBuilder targetEntityType( Class<?> targetEntityType ) {
		Assert.notNull( targetEntityType );
		this.targetEntityType = targetEntityType;
		return this;
	}

	/**
	 * Set the name of the property on the source end.  The property should be found in the corresponding
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} of the source
	 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration}.
	 * <p/>
	 * Should not be null.  If you want to remove the source property explicitly, use {@link #removeSourceProperty()}.
	 *
	 * @param sourceProperty name, not null
	 * @return current builder
	 */
	public EntityAssociationBuilder sourceProperty( String sourceProperty ) {
		Assert.notNull( sourceProperty );
		this.sourceProperty = sourceProperty;
		sourcePropertyRemoved = false;
		return this;
	}

	/**
	 * Remove the property on the source end.  This implies that the association is entirely determined by
	 * the target entity linking back to the source entity.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder removeSourceProperty() {
		sourcePropertyRemoved = true;
		return this;
	}

	/**
	 * Set the name of the property on the target end.  The property should be found in the corresponding
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} of the target
	 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration}.
	 * <p/>
	 * Should not be null.  If you want to remove the target property explicitly, use {@link #removeTargetProperty()}.
	 *
	 * @param targetProperty name, not null
	 * @return current builder
	 */
	public EntityAssociationBuilder targetProperty( String targetProperty ) {
		Assert.notNull( targetProperty );
		this.targetProperty = targetProperty;
		targetPropertyRemoved = false;
		return this;
	}

	/**
	 * Remove the property on the target end.  This implies that the association is entirely determined by
	 * the source entity linking to the target entity, without a back link.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder removeTargetProperty() {
		targetPropertyRemoved = true;
		return this;
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
		MutableEntityAssociation association = configuration.association( name );

		if ( association == null ) {
			if ( targetEntityType != null ) {
				association = configuration.createAssociation( name );
			}
			else {
				// If no target specified, do not create this association, just ignore this builder
				return;
			}
		}

		if ( targetEntityType != null ) {
			EntityConfiguration targetEntityConfiguration = entityRegistry.getEntityConfiguration( targetEntityType );

			if ( targetEntityConfiguration == null ) {
				throw new IllegalArgumentException(
						"Entity type " + targetEntityType.getName() + " is not registered" );
			}

			association.setTargetEntityConfiguration( targetEntityConfiguration );
		}

		if ( sourcePropertyRemoved ) {
			association.setSourceProperty( null );
		}
		else if ( sourceProperty != null ) {
			EntityPropertyDescriptor sourcePropertyDescriptor =
					association.getSourceEntityConfiguration().getPropertyRegistry().getProperty( sourceProperty );

			if ( sourcePropertyDescriptor == null ) {
				throw new IllegalArgumentException(
						"Property " + sourceProperty + " was not found as source property for association " + name );
			}

			association.setSourceProperty( sourcePropertyDescriptor );
		}

		if ( targetPropertyRemoved ) {
			association.setTargetProperty( null );
		}
		else if ( targetProperty != null ) {
			EntityPropertyDescriptor targetPropertyDescriptor =
					association.getSourceEntityConfiguration().getPropertyRegistry().getProperty( targetProperty );

			if ( targetPropertyDescriptor == null ) {
				throw new IllegalArgumentException(
						"Property " + targetProperty + " was not found as target property for association " + name );
			}

			association.setTargetProperty( targetPropertyDescriptor );
		}

		applyAttributes( association );
		applyViewBuilders( association );
	}

	void postProcess( MutableEntityConfiguration configuration ) {
		MutableEntityAssociation association = configuration.association( name );

		for ( PostProcessor<MutableEntityAssociation> postProcessor : postProcessors() ) {
			postProcessor.process( association );
		}
	}
}
