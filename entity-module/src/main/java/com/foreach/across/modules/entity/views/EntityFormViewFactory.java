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
import com.foreach.across.modules.entity.views.elements.ViewElements;
import com.foreach.across.modules.entity.views.elements.button.ButtonViewElement;
import com.foreach.across.modules.entity.views.elements.container.ContainerViewElement;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import org.springframework.ui.ModelMap;

/**
 * @author Arne Vandamme
 */
public class EntityFormViewFactory<V extends ViewCreationContext> extends ConfigurablePropertiesEntityViewFactorySupport<V, EntityFormView>
{
	@Override
	protected ViewElements customizeViewElements( ViewElements elements ) {
		ContainerViewElement container = new ContainerViewElement();
		container.setName( "_formElements" );
		container.addAll( elements );

		ContainerViewElement rootElements = new ContainerViewElement();
		rootElements.setName( "_root" );
		rootElements.add( container );

		return rootElements;
	}

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
		view.addAttribute( "existing", !newEntity );
		view.setFormAction( newEntity
				                    ? view.getEntityLinkBuilder().create()
				                    : view.getEntityLinkBuilder().update( original )
		);

		EntityMessages messages = view.getEntityMessages();

		ContainerViewElement buttons = new ContainerViewElement();
		buttons.setName( "butttons" );

		ButtonViewElement save = new ButtonViewElement();
		save.setName( "btn-save" );
		save.setElementType( CommonViewElements.SUBMIT_BUTTON );
		save.setLabel( messages.messageWithFallback( "actions.save" ) );
		buttons.add( save );

		ButtonViewElement cancel = new ButtonViewElement();
		cancel.setName( "btn-cancel" );
		cancel.setElementType( CommonViewElements.LINK_BUTTON );
		cancel.setLink( view.getEntityLinkBuilder().overview() );
		cancel.setLabel( messages.messageWithFallback( "actions.cancel" ) );
		buttons.add( cancel );

		view.getEntityProperties().add( buttons );
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
	protected EntityFormView createEntityView( ModelMap model ) {
		return new EntityFormView( model );
	}
}
