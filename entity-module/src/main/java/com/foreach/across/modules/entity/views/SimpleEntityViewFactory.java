package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import org.springframework.ui.ModelMap;

/**
 * Default implementation of a {@link com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport} that
 * can be used to represent a read or write view of a number of entity properties.
 */
public class SimpleEntityViewFactory extends ConfigurablePropertiesEntityViewFactorySupport
{
	private ViewElementMode mode = ViewElementMode.FOR_READING;

	@Override
	public ViewElementMode getMode() {
		return mode;
	}

	public void setMode( ViewElementMode mode ) {
		this.mode = mode;
	}

	@Override
	protected void extendViewModel( ViewCreationContext viewCreationContext, EntityView view ) {

	}

	@Override
	protected EntityView createEntityView( ModelMap model ) {
		return new EntityView( model );
	}
}
