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
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

/**
 * Uses a backing {@link org.springframework.ui.ModelMap} for storing and fetching the different attributes.
 * Has a {@link com.foreach.across.modules.entity.views.EntityView#getTemplate()} method that refers to the rendering
 * template; as such it can be seen as an alternative to {@link org.springframework.web.servlet.ModelAndView} without
 * putting dependencies on Spring mvc.
 * <p/>
 * The backing model can be (partially) prefilled and passed in externally.  Default
 * {@link com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport} implementations use the model as
 * communication channel for data attributes between preparation and creation steps of a
 * {@link com.foreach.across.modules.entity.views.EntityView}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport
 */
public class EntityView implements Model
{
	public static final String ATTRIBUTE_VIEW_NAME = "entityViewName";
	public static final String ATTRIBUTE_ENTITY = "entity";
	public static final String ATTRIBUTE_ENTITY_CONFIGURATION = "entityConfiguration";
	public static final String ATTRIBUTE_ENTITY_LINKS = "entityLinks";
	public static final String ATTRIBUTE_MESSAGES = "messages";
	public static final String ATTRIBUTE_PROPERTIES = "properties";
	public static final String ATTRIBUTE_ENTITY_MENU = "entityMenu";
	public static final String ATTRIBUTE_PAGE_TITLE = "pageTitle";

	// Will contain the entity that is the parent of the association, if applicable
	public static final String ATTRIBUTE_PARENT_ENTITY = "parentEntity";

	private final ModelMap model;

	private String template;

	public EntityView( ModelMap model ) {
		Assert.notNull( model );
		this.model = model;
	}

	public String getName() {
		return getAttribute( ATTRIBUTE_VIEW_NAME );
	}

	public void setName( String name ) {
		model.put( ATTRIBUTE_VIEW_NAME, name );
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate( String template ) {
		this.template = template;
	}

	public EntityConfiguration getEntityConfiguration() {
		return getAttribute( ATTRIBUTE_ENTITY_CONFIGURATION );
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		model.addAttribute( ATTRIBUTE_ENTITY_CONFIGURATION, entityConfiguration );
	}

	public EntityLinkBuilder getEntityLinkBuilder() {
		return getAttribute( ATTRIBUTE_ENTITY_LINKS );
	}

	public void setEntityLinkBuilder( EntityLinkBuilder entityLinks ) {
		model.addAttribute( ATTRIBUTE_ENTITY_LINKS, entityLinks );
	}

	public EntityMessages getEntityMessages() {
		return getAttribute( ATTRIBUTE_MESSAGES );
	}

	public void setEntityMessages( EntityMessages messages ) {
		model.addAttribute( ATTRIBUTE_MESSAGES, messages );
	}

	@SuppressWarnings("unchecked")
	public ViewElements getEntityProperties() {
		return getAttribute( ATTRIBUTE_PROPERTIES );
	}

	public void setEntityProperties( ViewElements entityProperties ) {
		model.put( ATTRIBUTE_PROPERTIES, entityProperties );
	}

	@SuppressWarnings("unchecked")
	public <V> V getEntity() {
		return (V) getAttribute( ATTRIBUTE_ENTITY );
	}

	public void setEntity( Object entity ) {
		model.put( ATTRIBUTE_ENTITY, entity );
	}

	/**
	 * @return The parent entity in case of an associated entity view.  The parent is usually the context for which
	 * the current entity is being viewed.
	 */
	public Object getParentEntity() {
		return getAttribute( ATTRIBUTE_PARENT_ENTITY );
	}

	public void setParentEntity( Object entity ) {
		addAttribute( ATTRIBUTE_PARENT_ENTITY, entity );
	}

	public void setPageTitle( String pageTitle ) {
		model.put( ATTRIBUTE_PAGE_TITLE, pageTitle );
	}

	public String getPageTitle() {
		return getAttribute( ATTRIBUTE_PAGE_TITLE );
	}

	public void setEntityMenu( Menu menu ) {
		model.put( ATTRIBUTE_ENTITY_MENU, menu );
	}

	public Menu getEntityMenu() {
		return getAttribute( ATTRIBUTE_ENTITY_MENU );
	}

	@SuppressWarnings("unchecked")
	public <V> V getAttribute( String attributeName ) {
		return (V) model.get( attributeName );
	}

	@Override
	public Model addAttribute( String attributeName, Object attributeValue ) {
		model.addAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public Model addAttribute( Object attributeValue ) {
		model.addAttribute( attributeValue );
		return this;
	}

	@Override
	public Model addAllAttributes( Collection<?> attributeValues ) {
		model.addAllAttributes( attributeValues );
		return this;
	}

	@Override
	public Model addAllAttributes( Map<String, ?> attributes ) {
		model.addAllAttributes( attributes );
		return this;
	}

	@Override
	public Model mergeAttributes( Map<String, ?> attributes ) {
		model.mergeAttributes( attributes );
		return this;
	}

	@Override
	public boolean containsAttribute( String attributeName ) {
		return model.containsAttribute( attributeName );
	}

	@Override
	public Map<String, Object> asMap() {
		return model;
	}
}
