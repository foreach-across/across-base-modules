package com.foreach.across.modules.entity.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.services.EntityRegistryImpl;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

@AcrossEventHandler
public class MenuEventsHandler
{
	@Autowired
	private EntityRegistryImpl entityRegistry;

	@Event
	public void adminMenu( AdminMenuEvent adminMenuEvent ) {
		PathBasedMenuBuilder builder = adminMenuEvent.builder();
		builder.item( "/entities", "Entity management" );

		for ( EntityConfiguration entity : entityRegistry.getEntities() ) {
			builder
					.item( "/entities/" + entity.getPath(), entity.getName() ).and()
					.item( "/entities/" + entity.getPath() + "/create",
					       "Create a new " + StringUtils.lowerCase( entity.getName() ) );
		}
	}

	@Event
	public void entityMenu( EntityAdminMenuEvent<IdBasedEntity> menu ) {
		PathBasedMenuBuilder builder = menu.builder();

		if ( menu.isForUpdate() ) {
			builder.item(
					"/entities/" + ( menu.getEntityType().getSimpleName().toLowerCase() ) + "/" + menu.getEntity()
					                                                                                   .getId(),
					"General" )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}
		else {
			builder.item( "/entities/" + ( menu.getEntityType().getSimpleName().toLowerCase() ) + "/create",
			              "General" )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}
	}
}
