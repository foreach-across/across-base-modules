/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.registrars.repository.*;
import com.foreach.across.modules.entity.registrars.repository.handlers.ManyToManyEntityAssociationBuilder;
import com.foreach.across.modules.entity.registrars.repository.handlers.ManyToOneEntityAssociationBuilder;
import com.foreach.across.modules.entity.registrars.repository.handlers.OneToManyEntityAssociationBuilder;
import com.foreach.across.modules.entity.registry.builders.EntityPropertyRegistryMappingMetaDataBuilder;
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

	@Bean
	protected ManyToOneEntityAssociationBuilder manyToOneAssociationViewBuilder() {
		return new ManyToOneEntityAssociationBuilder();
	}

	@Bean
	protected ManyToManyEntityAssociationBuilder manyToManyAssociationViewBuilder() {
		return new ManyToManyEntityAssociationBuilder();
	}

	@Bean
	protected OneToManyEntityAssociationBuilder oneToManyAssociationViewBuilder() {
		return new OneToManyEntityAssociationBuilder();
	}

	@Bean
	protected EntityPropertyRegistryMappingMetaDataBuilder entityPropertyMappingMetaDataBuilder() {
		return new EntityPropertyRegistryMappingMetaDataBuilder();
	}
}
