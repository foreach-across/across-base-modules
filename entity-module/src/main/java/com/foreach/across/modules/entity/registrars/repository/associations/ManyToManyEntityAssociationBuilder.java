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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.fetchers.AssociationJpaSpecificationListViewPageFetcher;
import com.foreach.across.modules.entity.views.fetchers.AssociationPropertyListViewPageFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToMany;

/**
 * @author Andy Somers
 */
@Component
public class ManyToManyEntityAssociationBuilder implements EntityAssociationBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( ManyToManyEntityAssociationBuilder.class );

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public boolean supports( PersistentProperty<?> sourceProperty ) {
		return sourceProperty.isAnnotationPresent( ManyToMany.class );
	}

	@Override
	public void buildAssociation( MutableEntityRegistry entityRegistry,
	                              MutableEntityConfiguration entityConfiguration,
	                              PersistentProperty property ) {
		MutableEntityConfiguration other
				= entityRegistry.getMutableEntityConfiguration( property.getActualType() );

		if ( other != null ) {
			String associationName = entityConfiguration.getName() + "." + property.getName();

			// Create source to target association
			MutableEntityAssociation association = entityConfiguration.association( associationName );

			if ( association == null ) {
				association = entityConfiguration.createAssociation( associationName );
				association.setTargetEntityConfiguration( other );
				association.setSourceProperty(
						entityConfiguration.getPropertyRegistry().getProperty( property.getName() )
				);
				association.addAttribute( PersistentProperty.class, property );

				buildCreateView( association );
				buildListView( association );
			}
			else {
				LOG.info( "Skipping automatic registration of association {} on {} as it is already registered.",
				          associationName, entityConfiguration.getName() );
			}

			// Create target to source association (reverse)
			association = other.association( associationName );

			if ( association == null ) {
				association = other.createAssociation( associationName );

				association.setTargetEntityConfiguration( entityConfiguration );
				association.setTargetProperty(
						entityConfiguration.getPropertyRegistry().getProperty( property.getName() )
				);
				association.addAttribute( PersistentProperty.class, property );

				buildCreateView( association );
				buildListView( association );
			}
			else {
				LOG.info( "Skipping automatic registration of association {} on {} as it is already registered.",
				          associationName, other.getName() );
			}
		}
	}

	public void buildListView( MutableEntityAssociation association ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityListViewFactory viewFactory = beanFactory.getBean( EntityListViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityListView.VIEW_NAME ), viewFactory );

		viewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".listView",
		                                "entityViews.listView",
		                                "entityViews" );

		viewFactory.setPageFetcher( buildManyToManyListViewPageFetcher( association ) );

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

	private EntityListViewPageFetcher buildManyToManyListViewPageFetcher( MutableEntityAssociation association ) {
		EntityPropertyDescriptor source = association.getSourceProperty();

		if ( source != null ) {
			return new AssociationPropertyListViewPageFetcher( source.getName() );
		}
		else {
			// Reverse association
			Repository repository = association.getTargetEntityConfiguration().getAttribute( Repository.class );

			if ( repository != null ) {
				if ( repository instanceof JpaSpecificationExecutor ) {
					return new AssociationJpaSpecificationListViewPageFetcher(
							association.getTargetProperty().getName(), (JpaSpecificationExecutor) repository );
				}
			}
		}

		return null;
	}
}
