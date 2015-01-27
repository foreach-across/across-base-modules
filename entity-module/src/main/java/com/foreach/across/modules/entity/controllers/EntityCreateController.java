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
package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * @author Arne Vandamme
 */
@AdminWebController
@RequestMapping(EntityController.PATH + "/{entityConfig}/create")
public class EntityCreateController extends EntityControllerSupport
{
	@Autowired
	private MenuFactory menuFactory;

	@ModelAttribute(EntityView.ATTRIBUTE_ENTITY)
	public Object entity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration ) throws Exception {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		return entityModel.createNew();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView saveEntity( @PathVariable("entityConfig") EntityConfiguration entityConfiguration,
	                                @ModelAttribute(EntityView.ATTRIBUTE_ENTITY) @Valid Object entity,
	                                BindingResult bindingResult,
	                                Model model,
	                                RedirectAttributes redirectAttributes ) {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		if ( !bindingResult.hasErrors() ) {
			entityModel.save( entity );

			ModelAndView mav = new ModelAndView();
			mav.setViewName(
					"redirect:" + entityConfiguration.getAttribute( EntityLinkBuilder.class ).update( entity )
			);

			redirectAttributes.addFlashAttribute( "successMessage", "feedback.entityCreated" );

			return mav;
		}
		else {
			return showCreateEntityForm( entityConfiguration, model );
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showCreateEntityForm(
			@PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration, Model model ) {
		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu<>( entityConfiguration.getEntityType() ) ) );

		EntityViewFactory view = entityConfiguration.getViewFactory( EntityFormView.CREATE_VIEW_NAME );
		return view.create( entityConfiguration, model );
	}
}
