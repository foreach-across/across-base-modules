package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.entity.config.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.controllers.EntityController;
import com.foreach.across.modules.entity.controllers.EntitySaveController;
import com.foreach.across.modules.entity.handlers.MenuEventsHandler;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "AdminWebModule")
@Configuration
public class AdminWebConfiguration implements EntityConfigurer
{
	@Autowired
	private AdminWeb adminWeb;

	@Bean
	public MenuEventsHandler menuEventsHandler() {
		return new MenuEventsHandler();
	}

	@Bean
	public EntityController entityController() {
		return new EntityController();
	}

	@Bean
	public EntitySaveController entitySaveController() {
		return new EntitySaveController();
	}

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.addPostProcessor( new PostProcessor<MutableEntityConfiguration>()
		{
			@Override
			public MutableEntityConfiguration process( MutableEntityConfiguration configuration ) {
				configuration.addAttribute( EntityLinkBuilder.class,
				                            new EntityLinkBuilder( adminWeb, EntityController.PATH, configuration ) );
				return configuration;
			}
		} );
	}
}
