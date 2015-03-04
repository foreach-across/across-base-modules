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
package com.foreach.across.modules.entity.registrars.repository.associations;

import com.foreach.across.modules.entity.query.EntityQueryPageFetcher;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.fetchers.AssociationListViewPageFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToOne;

/**
 * Builds the association in the opposite direction.
 *
 * @author Andy Somers, Arne Vandamme
 */
@Component
public class ManyToOneEntityAssociationBuilder implements EntityAssociationBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( ManyToOneEntityAssociationBuilder.class );

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public boolean supports( PersistentProperty<?> sourceProperty ) {
		return sourceProperty.isAnnotationPresent( ManyToOne.class );
	}

	@Override
	public void buildAssociation( MutableEntityRegistry entityRegistry,
	                              MutableEntityConfiguration to,
	                              PersistentProperty property ) {
		MutableEntityConfiguration from = entityRegistry.getMutableEntityConfiguration( property.getActualType() );
		if ( from != null && canAssociationBeBuilt( from, to ) ) {
			String associationName = to.getName() + "." + property.getName();

			MutableEntityAssociation association = from.createAssociation( associationName );
			association.setTargetEntityConfiguration( to );
			association.setTargetProperty( to.getPropertyRegistry().getProperty( property.getName() ) );

			buildCreateView( association );
			buildListView( association, property );
		}
	}

	private boolean canAssociationBeBuilt( MutableEntityConfiguration from, MutableEntityConfiguration to ) {
		if ( !to.hasAttribute( EntityQueryPageFetcher.class ) ) {
			LOG.warn(
					"Unable to build association between {} and {} because {} does not provide an EntityQueryPageFetcher.",
					from.getName(), to.getName(), to.getName() );
			return false;
		}

		return true;
	}

	public void buildListView( MutableEntityAssociation association, final PersistentProperty property ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityListViewFactory viewFactory = beanFactory.getBean( EntityListViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityListView.VIEW_NAME ), viewFactory );

		viewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".listView",
		                                "entityViews.listView",
		                                "entityViews" );

		EntityQueryPageFetcher queryPageFetcher = to.getAttribute( EntityQueryPageFetcher.class );

		if ( queryPageFetcher != null ) {
			viewFactory.setPageFetcher(
					new AssociationListViewPageFetcher( association.getTargetProperty(), queryPageFetcher )
			);
		}
		else {
			LOG.warn( "Unable to create ManyToOne association {} as there is no EntityQueryPageFetcher available",
			          association.getName() );
		}

		association.registerView( EntityListView.VIEW_NAME, viewFactory );
	}

	public void buildCreateView( MutableEntityAssociation association ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityFormViewFactory viewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityFormView.CREATE_VIEW_NAME ), viewFactory );
		viewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".createView",
		                                "entityViews.createView",
		                                "entityViews" );

		association.registerView( EntityFormView.CREATE_VIEW_NAME, viewFactory );

	}
}
