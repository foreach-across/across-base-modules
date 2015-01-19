package com.foreach.across.modules.entity.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

@AcrossEventHandler
public class MenuEventsHandler
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Event
	public void adminMenu( AdminMenuEvent adminMenuEvent ) {
		PathBasedMenuBuilder builder = adminMenuEvent.builder();
		builder.item( "/entities", "Entity management" );

		for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
			builder
					.item( "/entities/" + entityConfiguration.getName(), entityConfiguration.getDisplayName() ).and()
					.item( "/entities/" + entityConfiguration.getName() + "/create",
					       "Create a new " + StringUtils.uncapitalize( entityConfiguration.getDisplayName() ) );
		}
	}

	@Event
	public void entityMenu( EntityAdminMenuEvent<IdBasedEntity> menu ) {
		PathBasedMenuBuilder builder = menu.builder();

		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( menu.getEntityType() );

		if ( menu.isForUpdate() ) {
			builder.item(
					"/entities/" + entityConfiguration.getName() + "/" + entityConfiguration.getEntityModel().getId(
							menu.getEntity() ),
					"General" )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}
		else {
			builder.item( "/entities/" + entityConfiguration.getName() + "/create",
			              "General" )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}
	}
}
