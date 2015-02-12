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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registrars.repository.handlers.AssociationViewBuilder;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.mapping.JpaPersistentProperty;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Collection;

/**
 * Builds the list of associations and their views.
 *
 * @author Arne Vandamme
 */
public class RepositoryEntityAssociationsBuilder
{
	@Autowired
	private Collection<AssociationViewBuilder> associationViewBuilders;

	public <T> void buildAssociations( final MutableEntityRegistry entityRegistry,
	                                   final MutableEntityConfiguration entityConfiguration ) {
		RepositoryFactoryInformation<T, ?> repositoryFactoryInformation
				= entityConfiguration.getAttribute( RepositoryFactoryInformation.class );

		if ( repositoryFactoryInformation != null ) {
			PersistentEntity persistentEntity = repositoryFactoryInformation.getPersistentEntity();

			persistentEntity.doWithAssociations( new SimpleAssociationHandler()
			{
				@Override
				public void doWithAssociation( Association<? extends PersistentProperty<?>> association ) {
					PersistentProperty property = association.getInverse();

					if ( property instanceof JpaPersistentProperty ) {
						JpaPersistentProperty jpaProperty = (JpaPersistentProperty) property;

						if ( jpaProperty.isAnnotationPresent( ManyToOne.class ) ) {
							MutableEntityConfiguration other
									= entityRegistry.getMutableEntityConfiguration( property.getActualType() );

							if ( other != null ) {
								createAssociationFromTo( ManyToOne.class, other, entityConfiguration, property );
							}
						}

						if ( jpaProperty.isAnnotationPresent( ManyToMany.class ) ) {
							MutableEntityConfiguration other
									= entityRegistry.getMutableEntityConfiguration( property.getActualType() );

							if ( other != null ) {
								createAssociationFromTo( ManyToMany.class, entityConfiguration, other, property );
								createAssociationFromTo( ManyToMany.class, other, entityConfiguration, property );
							}
						}
					}
				}
			} );
		}
	}

	private void createAssociationFromTo( Class<?> relationShipClass,
	                                      MutableEntityConfiguration from,
	                                      MutableEntityConfiguration to,
	                                      PersistentProperty property ) {
		MutableEntityAssociation association = from.createAssociation( to );
		association.addAttribute( PersistentProperty.class, property );

		for ( AssociationViewBuilder builder : associationViewBuilders ) {
			if ( builder.supports( relationShipClass ) ) {
				builder.buildCreateView( association );
				builder.buildListView( association, property );
			}
		}
	}
}
