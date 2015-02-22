package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.views.EntityViewFactory;

public interface EntityViewRegistry
{
	boolean hasView( String viewName );

	<Y extends EntityViewFactory> Y getViewFactory( String viewName );
}
