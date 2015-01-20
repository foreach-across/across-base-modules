package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.registrars.repository.RepositoryEntityModelBuilder;
import com.foreach.across.modules.entity.registrars.repository.RepositoryEntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registrars.repository.RepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registrars.repository.RepositoryEntityViewsBuilder;
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
}
