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

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.controllers.EntityOverviewController;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityListView;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Lists all entities of a particular type.
 */
@AdminWebController
@RequestMapping(EntityListController.PATH)
@SuppressWarnings("unchecked")
public class EntityListController extends EntityControllerSupport
{
	public static final String PATH = EntityOverviewController.PATH + PATH_ENTITY;

	@ModelAttribute(VIEW_REQUEST)
	public Object buildViewRequest(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			NativeWebRequest request,
			ExtendedModelMap model ) {
		return super.buildViewRequest( entityConfiguration, false, false, null, request, model );
	}

	@Override
	protected String getDefaultViewName() {
		return EntityListView.VIEW_NAME;
	}

	@RequestMapping
	public ModelAndView listAllEntities(
			@ModelAttribute(VIEW_REQUEST) EntityViewRequest viewRequest,
			Model model,
			Pageable pageable
	) {
		model.addAttribute( EntityListView.ATTRIBUTE_PAGEABLE, pageable );

		return viewRequest.createView( model );
	}

}
