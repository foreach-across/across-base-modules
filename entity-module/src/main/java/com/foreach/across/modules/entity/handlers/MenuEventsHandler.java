package com.foreach.across.modules.entity.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.util.List;

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
			EntityMessageCodeResolver messageCodeResolver = entityConfiguration.getEntityMessageCodeResolver();
			EntityMessages messages = new EntityMessages( messageCodeResolver );

			builder
					.item( "/entities/" + entityConfiguration.getName(), messageCodeResolver.getNameSingular() )
					.and()
					.item( "/entities/" + entityConfiguration.getName() + "/create",
					       messages.createAction()
					);

		}
	}

	@Event
	@SuppressWarnings("unchecked")
	public void entityMenu( EntityAdminMenuEvent<IdBasedEntity> menu ) {
		PathBasedMenuBuilder builder = menu.builder();

		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( menu.getEntityType() );

		if ( menu.isForUpdate() ) {
			String path = "/entities/" + entityConfiguration.getName() + "/"
					+ entityConfiguration.getId( menu.getEntity() );
			builder.item( path, "General" ).order( Ordered.HIGHEST_PRECEDENCE );

			// Get associations

			List<Class> associations = entityConfiguration.getAttribute( "associations" );

			if ( associations != null ) {
				for ( Class associationType : associations ) {
					EntityConfiguration associated = entityRegistry.getEntityConfiguration( associationType );
					EntityMessageCodeResolver messageCodeResolver = associated.getEntityMessageCodeResolver();

					builder.item(
							associated.getName(),
							messageCodeResolver.getNamePlural(),
							path + "/associations/" + associated.getName()
					);
				}
			}
		}
		else {
			builder.item( "/entities/" + entityConfiguration.getName() + "/create",
			              "General" )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}
	}
}
