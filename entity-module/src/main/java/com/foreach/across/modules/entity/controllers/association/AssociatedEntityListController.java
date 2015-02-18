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

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.controllers.entity.EntityViewController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.Serializable;

/**
 * @author Arne Vandamme
 */
@AdminWebController
@RequestMapping(AssociatedEntityListController.PATH)
public class AssociatedEntityListController extends AssociatedEntityControllerSupport
{
	public static final String PATH = EntityViewController.PATH + PATH_ASSOCIATION;

	@Autowired
	private MenuFactory menuFactory;

	@ModelAttribute(VIEW_REQUEST)
	public Object buildViewRequest(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@PathVariable(VAR_ENTITY_ID) Serializable entityId,
			@PathVariable(VAR_ASSOCIATION) String associationName,
			NativeWebRequest request,
			ModelMap model ) {
		return super.buildViewRequest(
				entityConfiguration, entityId, associationName, false, false, null, request, model
		);
	}

	@Override
	protected String getDefaultViewName() {
		return EntityListView.VIEW_NAME;
	}

	@RequestMapping
	@SuppressWarnings("unchecked")
	public String listAllEntities(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@ModelAttribute(ATTRIBUTE_SOURCE_ENTITY) Object sourceEntity,
			@ModelAttribute(VIEW_REQUEST) EntityViewRequest viewRequest,
			ModelMap model,
			Pageable pageable
	) {
		model.addAttribute( EntityListView.ATTRIBUTE_PAGEABLE, pageable );

		EntityView entityView = viewRequest.createView( model );
		entityView.setPageTitle(
				new EntityMessages( entityConfiguration.getEntityMessageCodeResolver() )
						.updatePageTitle( entityConfiguration.getLabel( sourceEntity ) )
		);
		entityView.setEntityMenu(
				menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(), sourceEntity ) )
		);

		return entityView.getTemplate();
	}
}
