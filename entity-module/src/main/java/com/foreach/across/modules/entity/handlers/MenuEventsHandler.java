package com.foreach.across.modules.entity.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.AssociatedEntityLinkBuilder;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
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
			EntityMessageCodeResolver messageCodeResolver = entityConfiguration.getEntityMessageCodeResolver();
			EntityMessages messages = new EntityMessages( messageCodeResolver );
			EntityLinkBuilder linkBuilder = entityConfiguration.getAttribute( EntityLinkBuilder.class );
			AcrossModuleInfo moduleInfo = entityConfiguration.getAttribute( AcrossModuleInfo.class );

			String group = "/entities";

			if ( moduleInfo != null ) {
				group = "/entities/" + moduleInfo.getName();
				builder.group( group, moduleInfo.getName() ).disable();
			}

			builder.item( group + "/" + entityConfiguration.getName(),
			              messageCodeResolver.getNameSingular(),
			              linkBuilder.overview() )
			       .and()
			       .item( group + "/" + entityConfiguration.getName() + "/create",
			              messages.createAction(),
			              linkBuilder.create()
			       );
		}
	}

	@Event
	@SuppressWarnings("unchecked")
	public void entityMenu( EntityAdminMenuEvent menu ) {
		PathBasedMenuBuilder builder = menu.builder();

		EntityConfiguration<Object> entityConfiguration = entityRegistry.getEntityConfiguration( menu.getEntityType() );
		EntityLinkBuilder linkBuilder = entityConfiguration.getAttribute( EntityLinkBuilder.class );

		if ( menu.isForUpdate() ) {
			builder.item( linkBuilder.update( menu.getEntity() ), "General" ).order( Ordered.HIGHEST_PRECEDENCE );

			// Get associations
			for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
				EntityConfiguration associated = association.getAssociatedEntityConfiguration();
				AssociatedEntityLinkBuilder associatedLinkBuilder = association.getAttribute(
						AssociatedEntityLinkBuilder.class );
				EntityMessageCodeResolver messageCodeResolver = associated.getEntityMessageCodeResolver();

				builder.item(
						association.getName(), messageCodeResolver.getNamePlural(),
						associatedLinkBuilder.overview( menu.getEntity() )
				);
			}
		}
		else {
			builder.item( linkBuilder.create(), "General" ).order( Ordered.HIGHEST_PRECEDENCE );
		}
	}
}
