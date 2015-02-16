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

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @author Niels Doucet, Arne Vandamme
 * @since 9/02/2015
 */
@AdminWebController
@RequestMapping(AssociatedEntityUpdateController.PATH)
public class AssociatedEntityUpdateController extends AssociatedEntityControllerSupport
{
	public static final String PATH = AssociatedEntityListController.PATH + PATH_ASSOCIATION_ID + "/update";

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private MenuFactory menuFactory;

	@Override
	protected String getDefaultViewName() {
		return EntityFormView.CREATE_VIEW_NAME;
	}

	@ModelAttribute(VIEW_REQUEST)
	public Object buildViewRequest(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@PathVariable(VAR_ENTITY_ID) Serializable entityId,
			@PathVariable(VAR_ASSOCIATION) String associationName,
			@PathVariable(VAR_ASSOCIATION_ID) Serializable associatedEntityId,
			NativeWebRequest request,
			ExtendedModelMap model ) {
		return super.buildViewRequest(
				entityConfiguration, entityId, associationName, true, true, associatedEntityId, request,
				model
		);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView saveEntity(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@ModelAttribute(ATTRIBUTE_SOURCE_ENTITY) Object sourceEntity,
			@PathVariable(VAR_ASSOCIATION) String associationName,
			@ModelAttribute(VIEW_REQUEST) @Valid EntityViewRequest viewRequest,
			BindingResult bindingResult,
			ExtendedModelMap model,
			RedirectAttributes redirectAttributes ) {
		EntityModel associatedModel =
				entityConfiguration.association( associationName ).getTargetEntityConfiguration().getEntityModel();

		if ( !bindingResult.hasErrors() ) {
			associatedModel.save( viewRequest.getEntity() );

			EntityLinkBuilder linkBuilder = (EntityLinkBuilder) model.get( EntityView.ATTRIBUTE_ENTITY_LINKS );

			ModelAndView mav = new ModelAndView();
			mav.setViewName( adminWeb.redirect( linkBuilder.update( viewRequest.getEntity() ) ) );

			redirectAttributes.addFlashAttribute( "successMessage", "feedback.entityUpdated" );

			return mav;
		}
		else {
			return showUpdateEntityForm( entityConfiguration, sourceEntity, viewRequest, model );
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showUpdateEntityForm(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@ModelAttribute(ATTRIBUTE_SOURCE_ENTITY) Object sourceEntity,
			@ModelAttribute(VIEW_REQUEST) EntityViewRequest viewRequest,
			Model model ) {
		EntityView view = viewRequest.createView( model );
		view.setPageTitle(
				new EntityMessages( entityConfiguration.getEntityMessageCodeResolver() )
						.updatePageTitle( entityConfiguration.getLabel( sourceEntity ) )
		);
		view.setEntityMenu(
				menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(), sourceEntity ) )
		);

		return view;
	}
}
