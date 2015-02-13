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

import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import com.foreach.across.modules.entity.views.elements.button.ButtonViewElement;

/**
 * @author Arne Vandamme
 */
public class EntityFormViewFactory<V extends ViewCreationContext> extends ConfigurablePropertiesEntityViewFactorySupport<V, EntityFormView>
{
	@SuppressWarnings("unchecked")
	@Override
	protected void extendViewModel( V viewCreationContext, EntityFormView view ) {
		EntityModel entityModel = viewCreationContext.getEntityConfiguration().getEntityModel();

		Object entity = retrieveOrCreateEntity( entityModel, view );
		view.setEntity( entity );

		Object original = view.getOriginalEntity();

		if ( original == null ) {
			original = entity;
		}

		boolean newEntity = entityModel.isNew( entity );
		view.addObject( "existing", !newEntity );
		view.setFormAction( newEntity
				                    ? view.getEntityLinkBuilder().create()
				                    : view.getEntityLinkBuilder().update( original )
		);

		ButtonViewElement button = new ButtonViewElement();
		button.setName( "btn-save" );
		button.setElementType( CommonViewElements.SUBMIT_BUTTON);
		button.setLabel( "Save" );

		ButtonViewElement cancel = new ButtonViewElement();
		cancel.setName( "btn-cancel" );
		cancel.setElementType( CommonViewElements.LINK_BUTTON );
		cancel.setLink( view.getEntityLinkBuilder().overview() );

		cancel.setLabel( "Cancel" );

		view.getEntityProperties().add( button );
		view.getEntityProperties().add( cancel );

	}

	private Object retrieveOrCreateEntity( EntityModel entityModel, EntityFormView view ) {
		Object entity = view.getEntity();

		if ( entity == null ) {
			entity = entityModel.createNew();
		}

		return entity;
	}

	@Override
	protected ViewElementMode getMode() {
		return ViewElementMode.FOR_WRITING;
	}

	@Override
	protected EntityFormView createEntityView() {
		return new EntityFormView();
	}
}
