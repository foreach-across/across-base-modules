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
package com.foreach.across.modules.entity.controllers.association;

import com.foreach.across.modules.entity.controllers.AbstractEntityModuleController;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.controllers.ViewRequestValidator;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.web.WebViewCreationContextImpl;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;

/**
 * @author Arne Vandamme
 */
public abstract class AssociatedEntityControllerSupport extends AbstractEntityModuleController
{
	private static final String ATTRIBUTE_DATABINDER = EntityViewRequest.class.getName() + ".DataBinder";

	protected final Logger LOG = LoggerFactory.getLogger( getClass() );

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ViewRequestValidator viewRequestValidator;

	protected Object buildViewRequest(
			EntityConfiguration entityConfiguration,
			Serializable entityId,
			String associationName,
			boolean includeEntity,
			boolean includeDto,
			Serializable associatedEntityId,
			NativeWebRequest request,
			ModelMap model
	) {
		WebViewCreationContextImpl viewCreationContext = new WebViewCreationContextImpl();
		viewCreationContext.setRequest( request );

		Object sourceEntity = buildSourceEntityModel( entityConfiguration, entityId, model );

		String requestedViewName = request.getParameter( "view" );
		String partialFragment = request.getParameter( WebTemplateInterceptor.PARTIAL_PARAMETER );

		String viewName = StringUtils.defaultString( requestedViewName, getDefaultViewName() );

		EntityAssociation association = entityConfiguration.association( associationName );

		if ( association.isHidden() ) {
			LOG.warn(
					"AssociatedEntityControllers for association {} on {} are disabled because the EntityAssociation is hidden",
					association.getName(), entityConfiguration.getName() );
			throw new AccessDeniedException( "Not allowed to manage this associated entity type." );
		}

		EntityViewFactory viewFactory = association.getViewFactory( viewName );
		model.addAttribute( VIEW_FACTORY, viewFactory );

		viewCreationContext.setEntityAssociation( association );
		model.addAttribute( CREATION_CONTEXT, viewCreationContext );

		EntityViewRequest viewRequest =
				new EntityViewRequest( viewName, viewFactory, viewCreationContext );
		model.addAttribute( VIEW_COMMAND, viewRequest );

		if ( StringUtils.isNotBlank( partialFragment ) ) {
			viewRequest.setPartialFragment( partialFragment );
		}

		if ( includeDto && !includeEntity ) {
			throw new RuntimeException( "Entity must be included if dto is requested." );
		}

		if ( includeEntity ) {
			if ( associatedEntityId != null ) {
				if ( includeDto ) {
					viewRequest.setEntity( buildUpdateDto( association.getTargetEntityConfiguration(),
					                                       associatedEntityId, model ) );
				}
				else {
					viewRequest.setEntity(
							buildOriginalEntityModel( association.getTargetEntityConfiguration(), associatedEntityId,
							                          model ) );
				}
			}
			else if ( includeDto ) {
				viewRequest.setEntity( buildNewEntityDto( sourceEntity, association, model ) );
			}
		}

		viewRequest.prepareModelAndCommand( model );

		request.setAttribute( EntityViewRequest.class.getName(), viewRequest, RequestAttributes.SCOPE_REQUEST );

		initViewFactoryBinder( request );

		return viewRequest;
	}

	protected boolean isDefaultView( String viewName ) {
		return StringUtils.equals( viewName, getDefaultViewName() );
	}

	protected abstract String getDefaultViewName();

	/**
	 * Create a DTO for a given entity.  This will load the source entity, add it to the model and then
	 * create a dto for it.
	 */
	@SuppressWarnings("unchecked")
	protected Object buildUpdateDto( EntityConfiguration entityConfiguration, Serializable entityId, ModelMap model ) {
		Object entity = buildOriginalEntityModel( entityConfiguration, entityId, model );

		EntityModel entityModel = entityConfiguration.getEntityModel();
		Object dto = entityModel.createDto( entity );

		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, dto );

		return dto;
	}

	/**
	 * Retrieve the source entity from the path - this is the source of the association.
	 */
	protected Object buildSourceEntityModel( EntityConfiguration<?> entityConfiguration,
	                                         Serializable entityId,
	                                         ModelMap model ) {
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );

		model.addAttribute( ATTRIBUTE_SOURCE_ENTITY, entity );

		return entity;
	}

	/**
	 * Retrieve the original entity from the path.
	 */
	protected Object buildOriginalEntityModel( EntityConfiguration<?> entityConfiguration,
	                                           Serializable entityId,
	                                           ModelMap model
	) {
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );

		model.addAttribute( EntityFormView.ATTRIBUTE_ORIGINAL_ENTITY, entity );
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		return entity;
	}

	protected Object buildNewEntityDto( Object sourceEntity, EntityAssociation association, ModelMap model ) {
		EntityModel entityModel = association.getTargetEntityConfiguration().getEntityModel();

		// todo: remove direct dependency on spring data - use a regular entitypropertydescriptor ?
		BeanWrapper associatedBeanWrapper = new BeanWrapperImpl( entityModel.createNew() );
		if ( association.getTargetProperty() != null ) {
			associatedBeanWrapper.setPropertyValue( association.getTargetProperty().getName(), sourceEntity );
		}

		Object entity = associatedBeanWrapper.getWrappedInstance();

		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		return entity;
	}

	@InitBinder(VIEW_REQUEST)
	protected void initBinder( @PathVariable(VAR_ENTITY) EntityConfiguration<?> entityConfiguration,
	                           WebRequest request,
	                           WebDataBinder binder ) {
		request.setAttribute( ATTRIBUTE_DATABINDER, binder, RequestAttributes.SCOPE_REQUEST );

		binder.setMessageCodesResolver( entityConfiguration.getEntityMessageCodeResolver() );
		binder.setValidator( viewRequestValidator );

		initViewFactoryBinder( request );
	}

	/**
	 * Allows the ViewFactory to customize DataBinder.  This should be done only once, however the calling order
	 * of @InitBinder and @ModelAttribute cannot be relied on, as such this method is called twice.  Dispatching
	 * to the ViewFactory should only be done once (before actual binding occurs).
	 */
	protected void initViewFactoryBinder( WebRequest request ) {
		EntityViewRequest viewRequest
				= (EntityViewRequest) request.getAttribute( EntityViewRequest.class.getName(),
				                                            RequestAttributes.SCOPE_REQUEST );
		WebDataBinder dataBinder
				= (WebDataBinder) request.getAttribute( ATTRIBUTE_DATABINDER, RequestAttributes.SCOPE_REQUEST );

		if ( viewRequest != null && dataBinder != null ) {
			viewRequest.initDataBinder( dataBinder );

			request.removeAttribute( ATTRIBUTE_DATABINDER, RequestAttributes.SCOPE_REQUEST );
		}
	}
}
