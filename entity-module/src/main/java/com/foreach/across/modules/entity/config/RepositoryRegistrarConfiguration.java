package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.registrars.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the {@link com.foreach.across.modules.entity.registrars.repository.RepositoryEntityRegistrar} and all
 * beans it depends on.  Ensures that Spring Data {@link org.springframework.data.repository.Repository}
 * implementations will get registered as entities.
 */
@Configuration
public class RepositoryRegistrarConfiguration
{
	@Bean
	public RepositoryEntityRegistrar crudRepositoryEntityRegistrar() {
		return new RepositoryEntityRegistrar();
	}

	@Bean
	protected RepositoryEntityModelBuilder repositoryEntityModelBuilder() {
		return new RepositoryEntityModelBuilder();
	}

	@Bean
	protected RepositoryEntityViewsBuilder repositoryEntityViewsBuilder() {
		return new RepositoryEntityViewsBuilder();
	}

	@Bean
	protected RepositoryEntityPropertyRegistryBuilder repositoryEntityPropertyRegistryBuilder() {
		return new RepositoryEntityPropertyRegistryBuilder();
	}

	@Bean
	protected RepositoryEntityAssociationsBuilder repositoryEntityAssociationsBuilder() {
		return new RepositoryEntityAssociationsBuilder();
	}
}
