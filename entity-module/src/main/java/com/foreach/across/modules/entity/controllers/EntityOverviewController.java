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
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Lists all entity types registered in the context.
 *
 * @author Arne Vandamme
 */
@AdminWebController
@RequestMapping(EntityOverviewController.PATH)
public class EntityOverviewController extends AbstractEntityModuleController
{
	public static final String PATH = EntityControllerAttributes.ROOT_PATH;

	@Autowired
	private EntityRegistryImpl entityRegistry;

	@RequestMapping
	public String listAllEntityTypes( ModelMap model ) {
		model.addAttribute( "entities", entityRegistry.getEntities() );

		return "th/entity/overview";
	}
}
