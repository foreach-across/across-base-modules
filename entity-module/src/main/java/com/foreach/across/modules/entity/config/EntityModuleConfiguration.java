package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.registrars.CrudRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registrars.ModuleEntityRegistration;
import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.entity.services.EntityFormRenderer;
import com.foreach.across.modules.entity.services.EntityRegistryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityModuleConfiguration
{
	@Bean
	public EntityRegistryImpl entityRegistry() {
		return new EntityRegistryImpl();
	}

	@Bean
	public ModuleEntityRegistration moduleEntityRegistration() {
		return new ModuleEntityRegistration();
	}

	@Bean
	public CrudRepositoryEntityRegistrar crudRepositoryEntityRegistrar() {
		return new CrudRepositoryEntityRegistrar();
	}

	@Bean
	public EntityFormFactory entityFormFactory() {
		return new EntityFormFactory();
	}

	@Bean
	public EntityFormRenderer entityFormRenderer() {
		return new EntityFormRenderer();
	}
/*
	@Bean
	public LocalValidatorFactoryBean entityValidatorFactory() {
		return new LocalValidatorFactoryBean();
	}
	*/
}
