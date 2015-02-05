package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.registry.handlers.EntityPropertyRegistryValidationConstraintHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author niels
 * @since 4/02/2015
 */
@Configuration
public class EntityPropertyRegistryHandlerConfiguration
{

	@Bean
	public EntityPropertyRegistryValidationConstraintHandler entityPropertyRegistryValidationConstraintHandler() {
		return new EntityPropertyRegistryValidationConstraintHandler();
	}
}
