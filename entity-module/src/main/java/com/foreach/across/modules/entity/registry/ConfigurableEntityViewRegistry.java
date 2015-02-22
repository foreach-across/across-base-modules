package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.views.EntityViewFactory;

public interface ConfigurableEntityViewRegistry extends EntityViewRegistry
{
	void registerView( String viewName, EntityViewFactory viewFactory );
}
