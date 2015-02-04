package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;

public interface EntityConfigurer
{
	void configure( EntitiesConfigurationBuilder configuration );
}
