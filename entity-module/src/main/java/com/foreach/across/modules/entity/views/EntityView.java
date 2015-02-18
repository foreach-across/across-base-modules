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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.elements.ViewElements;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.menu.Menu;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Arne Vandamme
 */
public class EntityView extends ModelAndView
{
	public static final String ATTRIBUTE_VIEW_NAME = "entityViewName";
	public static final String ATTRIBUTE_ENTITY = "entity";
	public static final String ATTRIBUTE_ENTITY_CONFIGURATION = "entityConfiguration";
	public static final String ATTRIBUTE_ENTITY_LINKS = "entityLinks";
	public static final String ATTRIBUTE_MESSAGES = "messages";
	public static final String ATTRIBUTE_PROPERTIES = "properties";
	public static final String ATTRIBUTE_ENTITY_MENU = "entityMenu";
	public static final String ATTRIBUTE_PAGE_TITLE = "pageTitle";

	public String getName() {
		return (String) getModelMap().get( ATTRIBUTE_VIEW_NAME );
	}

	public void setName( String name ) {
		getModelMap().put( ATTRIBUTE_VIEW_NAME, name );
	}

	public void addModel( Model model ) {
		addAllObjects( model.asMap() );
	}

	public EntityConfiguration getEntityConfiguration() {
		return (EntityConfiguration) getModelMap().get( ATTRIBUTE_ENTITY_CONFIGURATION );
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		getModelMap().addAttribute( ATTRIBUTE_ENTITY_CONFIGURATION, entityConfiguration );
	}

	public EntityLinkBuilder getEntityLinkBuilder() {
		return (EntityLinkBuilder) getModelMap().get( ATTRIBUTE_ENTITY_LINKS );
	}

	public void setEntityLinkBuilder( EntityLinkBuilder entityLinks ) {
		getModelMap().addAttribute( ATTRIBUTE_ENTITY_LINKS, entityLinks );
	}

	public EntityMessages getEntityMessages() {
		return (EntityMessages) getModelMap().get( ATTRIBUTE_MESSAGES );
	}

	public void setEntityMessages( EntityMessages messages ) {
		getModelMap().addAttribute( ATTRIBUTE_MESSAGES, messages );
	}

	@SuppressWarnings("unchecked")
	public ViewElements getEntityProperties() {
		return (ViewElements) getModelMap().get( ATTRIBUTE_PROPERTIES );
	}

	public void setEntityProperties( ViewElements entityProperties ) {
		getModelMap().put( ATTRIBUTE_PROPERTIES, entityProperties );
	}

	@SuppressWarnings("unchecked")
	public <V> V getEntity() {
		return (V) getModelMap().get( ATTRIBUTE_ENTITY );
	}

	public void setEntity( Object entity ) {
		getModelMap().put( ATTRIBUTE_ENTITY, entity );
	}

	public void setPageTitle( String pageTitle ) {
		getModelMap().put( ATTRIBUTE_PAGE_TITLE, pageTitle );
	}

	public String getPageTitle() {
		return (String) getModelMap().get( ATTRIBUTE_PAGE_TITLE );
	}

	public void setEntityMenu( Menu menu ) {
		getModelMap().put( ATTRIBUTE_ENTITY_MENU, menu );
	}

	public Menu getEntityMenu() {
		return (Menu) getModelMap().get( ATTRIBUTE_ENTITY_MENU );
	}
}
