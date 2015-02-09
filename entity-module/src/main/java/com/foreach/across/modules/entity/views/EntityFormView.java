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

/**
 * @author Arne Vandamme
 */
public class EntityFormView extends EntityView
{
	public static final String CREATE_VIEW_NAME = "createView";
	public static final String UPDATE_VIEW_NAME = "updateView";
	public static final String VIEW_TEMPLATE = "th/entity/edit";

	// Will contain the original (unmodified) entity for which the form is being rendered
	public static final String ATTRIBUTE_ORIGINAL_ENTITY = "originalEntity";
	public static final String ATTRIBUTE_FORM_ACTION = "formAction";

	// Will contain the entity that is the parent of the association, if applicable
	public static final String ATTRIBUTE_PARENT_ENTITY = "parentEntity";

	public Object getOriginalEntity() {
		return getModelMap().get( ATTRIBUTE_ORIGINAL_ENTITY );
	}

	public void setOriginalEntity( Object entity ) {
		getModelMap().put( ATTRIBUTE_ORIGINAL_ENTITY, entity );
	}

	public String getFormAction() {
		return (String) getModelMap().get( ATTRIBUTE_FORM_ACTION );
	}

	public void setFormAction( String action ) {
		getModelMap().put( ATTRIBUTE_FORM_ACTION, action );
	}

	public Object getParentEntity() {
		return getModelMap().get( ATTRIBUTE_PARENT_ENTITY );
	}

	public void setParentEntity( Object entity ) {
		getModelMap().put( ATTRIBUTE_PARENT_ENTITY, entity );
	}
}
