package com.foreach.across.modules.entity.views.elements;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.services.EntityFormService;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;

/**
 * Represents the builder context in which {@link com.foreach.across.modules.entity.views.elements.ViewElement}
 * instances should be constructed.  Typically a context is represented by:
 * <ul>
 * <li>the {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
 * of the entity that is holding the values</li>
 * <li>the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}
 * providing access to the different property descriptors</li>
 * <li>the {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver}
 * that should be used for resolving messages</li>
 * <li>the mode for which the view elements should be created</li>
 * <li>the {@link com.foreach.across.modules.entity.services.EntityFormService} that created this context
 * and can be used to retrieve builders</li>
 * </ul>
 *
 * @see com.foreach.across.modules.entity.services.EntityFormService
 * @see com.foreach.across.modules.entity.views.elements.ViewElementBuilder
 */
public class ViewElementBuilderContext
{
	private EntityConfiguration entityConfiguration;
	private EntityPropertyRegistry propertyRegistry;
	private EntityMessageCodeResolver messageCodeResolver;
	private EntityFormService entityFormService;
	private ViewElementMode viewElementMode;

	public EntityConfiguration getEntityConfiguration() {
		return entityConfiguration;
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
	}

	public EntityPropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	public EntityMessageCodeResolver getMessageCodeResolver() {
		return messageCodeResolver;
	}

	public void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	public EntityFormService getEntityFormService() {
		return entityFormService;
	}

	public void setEntityFormService( EntityFormService entityFormService ) {
		this.entityFormService = entityFormService;
	}

	public ViewElementMode getViewElementMode() {
		return viewElementMode;
	}

	public void setViewElementMode( ViewElementMode viewElementMode ) {
		this.viewElementMode = viewElementMode;
	}

	public ViewElementBuilder getBuilder( String property ) {
		return getBuilder( propertyRegistry.getProperty( property ) );
	}

	public ViewElementBuilder getBuilder( EntityPropertyDescriptor descriptor ) {
		if ( descriptor != null ) {
			return entityFormService.createBuilder( entityConfiguration, propertyRegistry, descriptor,
			                                        viewElementMode );
		}

		return null;
	}

	public ViewElement getViewElement( String property ) {
		return getViewElement( propertyRegistry.getProperty( property ) );
	}

	public ViewElement getViewElement( EntityPropertyDescriptor descriptor ) {
		if ( descriptor != null ) {
			ViewElementBuilder builder = getBuilder( descriptor );

			if ( builder != null ) {
				return builder.createViewElement( this );
			}
		}

		return null;
	}
}
