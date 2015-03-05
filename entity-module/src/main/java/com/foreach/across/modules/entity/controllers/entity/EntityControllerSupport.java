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
package com.foreach.across.modules.entity.controllers.entity;

import com.foreach.across.modules.entity.controllers.AbstractEntityModuleController;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.controllers.ViewRequestValidator;
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
 * Support class for controllers generating entity views linked to a particular entity type and/or entity.
 *
 * @author Arne Vandamme
 */
public abstract class EntityControllerSupport extends AbstractEntityModuleController
{
	private static final String ATTRIBUTE_DATABINDER = EntityViewRequest.class.getName() + ".DataBinder";

	protected final Logger LOG = LoggerFactory.getLogger( getClass() );

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ViewRequestValidator viewRequestValidator;

	protected Object buildViewRequest(
			EntityConfiguration entityConfiguration,
			boolean includeEntity,
			boolean includeDto,
			Serializable entityId,
			NativeWebRequest request,
			ModelMap model
	) {
		if ( entityConfiguration.isHidden() ) {
			LOG.warn( "EntityControllers for {} are disabled because the EntityConfiguration is hidden",
			          entityConfiguration.getName() );
			throw new AccessDeniedException( "Not allowed to manage this entity type." );
		}

		WebViewCreationContextImpl viewCreationContext = new WebViewCreationContextImpl();
		viewCreationContext.setRequest( request );

		String requestedViewName = request.getParameter( "view" );
		String partialFragment = request.getParameter( WebTemplateInterceptor.PARTIAL_PARAMETER );

		String viewName = StringUtils.defaultString( requestedViewName, getDefaultViewName() );

		EntityViewFactory viewFactory = entityConfiguration.getViewFactory( viewName );
		model.addAttribute( VIEW_FACTORY, viewFactory );

		viewCreationContext.setEntityConfiguration( entityConfiguration );
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
			if ( entityId != null ) {
				if ( includeDto ) {
					viewRequest.setEntity( buildUpdateDto( entityConfiguration, entityId, model ) );
				}
				else {
					viewRequest.setEntity( buildSourceEntityModel( entityConfiguration, entityId, model ) );
				}
			}
			else if ( includeDto ) {
				viewRequest.setEntity( buildNewEntityDto( entityConfiguration, model ) );
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
		Object entity = buildSourceEntityModel( entityConfiguration, entityId, model );

		EntityModel entityModel = entityConfiguration.getEntityModel();
		Object dto = entityModel.createDto( entity );

		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, dto );

		return dto;
	}

	/**
	 * Retrieve the source entity from the path.
	 */
	protected Object buildSourceEntityModel( EntityConfiguration<?> entityConfiguration,
	                                         Serializable entityId,
	                                         ModelMap model
	) {
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );

		model.addAttribute( EntityFormView.ATTRIBUTE_ORIGINAL_ENTITY, entity );
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		return entity;
	}

	protected Object buildNewEntityDto( EntityConfiguration<?> entityConfiguration, ModelMap model ) {
		EntityModel entityModel = entityConfiguration.getEntityModel();
		Object entity = entityModel.createNew();

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
