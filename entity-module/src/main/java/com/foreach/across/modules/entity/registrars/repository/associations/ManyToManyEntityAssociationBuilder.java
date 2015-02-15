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
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToMany;

/**
 * @author Andy Somers
 */
@Component
public class ManyToManyEntityAssociationBuilder implements EntityAssociationBuilder
{
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
			createAssociationFromTo( entityConfiguration, other, property );
			createAssociationFromTo( other, entityConfiguration, property );
		}
	}

	private void createAssociationFromTo( MutableEntityConfiguration from,
	                                      MutableEntityConfiguration to,
	                                      PersistentProperty property ) {
		MutableEntityAssociation association = from.createAssociation( property.getName() );
		association.setTargetEntityConfiguration( to );
		association.addAttribute( PersistentProperty.class, property );

		buildCreateView( association );
		buildListView( association, property );
	}

	public void buildListView( MutableEntityAssociation association,
	                           final PersistentProperty property ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityListViewFactory viewFactory = beanFactory.getBean( EntityListViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityListView.VIEW_NAME ), viewFactory );

		viewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".listView",
		                                "entityViews.listView",
		                                "entityViews" );

//		Repository repository = to.getAttribute( Repository.class );
//		if ( repository instanceof JpaSpecificationExecutor ) {
//			final JpaSpecificationExecutor jpa = (JpaSpecificationExecutor) repository;
//			viewFactory.setPageFetcher( new EntityListViewPageFetcher<WebViewCreationContext>()
//			{
//				@Override
//				public Page fetchPage( WebViewCreationContext viewCreationContext,
//				                       Pageable pageable,
//				                       final EntityView model ) {
//					/*BeanWrapper beanWrapper = new BeanWrapperImpl( model.getEntity());
//					beanWrapper.getPropertyValue(  )*/
//
//					Specification s = new Specification<Object>()
//					{
//						@Override
//						public Predicate toPredicate( Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder cb ) {
//							return cb.equal( root.get( property.getName() ), model.getEntity() );
//						}
//					};
//
//					return jpa.findAll( s, pageable );
//				}
//			} );
//		}
//		else if ( repository instanceof QueryDslPredicateExecutor ) {
//			System.err.println( "Repository not matching for: " + to.getName() );
//		}
//		else {
//			System.err.println( "Repository not matching for: " + to.getName() );
//		}

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
