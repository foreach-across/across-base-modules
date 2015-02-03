package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.config.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.controllers.AssociatedEntityController;
import com.foreach.across.modules.entity.controllers.EntityController;
import com.foreach.across.modules.entity.controllers.EntityCreateController;
import com.foreach.across.modules.entity.controllers.EntityUpdateController;
import com.foreach.across.modules.entity.handlers.MenuEventsHandler;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.web.AssociatedEntityLinkBuilder;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "AdminWebModule")
@Configuration
public class AdminWebConfiguration implements EntityConfigurer
{
	@Bean
	public MenuEventsHandler menuEventsHandler() {
		return new MenuEventsHandler();
	}

	@Bean
	public EntityController entityController() {
		return new EntityController();
	}

	@Bean
	public EntityCreateController entityCreateController() {
		return new EntityCreateController();
	}

	@Bean
	public EntityUpdateController entitySaveController() {
		return new EntityUpdateController();
	}

	@Bean
	public AssociatedEntityController associatedEntityController() {
		return new AssociatedEntityController();
	}

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.addPostProcessor( new PostProcessor<MutableEntityConfiguration<?>>()
		{
			@Override
			public MutableEntityConfiguration process( MutableEntityConfiguration<?> configuration ) {
				configuration.addAttribute( EntityLinkBuilder.class,
				                            new EntityLinkBuilder( EntityController.PATH, configuration ) );
				return configuration;
			}
		} );

		// Create associations
		configuration.addPostProcessor( new PostProcessor<MutableEntityConfiguration<?>>()
		{
			@Override
			public MutableEntityConfiguration process( MutableEntityConfiguration<?> configuration ) {
				EntityLinkBuilder parentLinkBuilder = configuration.getAttribute( EntityLinkBuilder.class );
				for ( EntityAssociation association : configuration.getAssociations() ) {
					EntityLinkBuilder originalLinkBuilder = association.getAssociatedEntityConfiguration()
					                                                   .getAttribute( EntityLinkBuilder.class );

					AssociatedEntityLinkBuilder associationLinkBuilder
							= new AssociatedEntityLinkBuilder( parentLinkBuilder, originalLinkBuilder );

					MutableEntityAssociation mutable = configuration.association( association.getEntityType() );
					mutable.addAttribute( AssociatedEntityLinkBuilder.class, associationLinkBuilder );
				}

				return configuration;
			}
		} );
	}
}
