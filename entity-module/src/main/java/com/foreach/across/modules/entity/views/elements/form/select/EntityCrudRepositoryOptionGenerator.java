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
package com.foreach.across.modules.entity.views.elements.form.select;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Builds {@link com.foreach.across.modules.entity.views.elements.form.select.SelectOption} instances
 * by fetching all entities from a Repository implementation.
 * <p/>
 * Supports sorting the results.
 *
 * @author Arne Vandamme
 */
public class EntityCrudRepositoryOptionGenerator implements SelectOptionGenerator
{
	private final EntityConfiguration entityConfiguration;
	private final CrudRepository repository;

	public EntityCrudRepositoryOptionGenerator( EntityConfiguration entityConfiguration,
	                                            CrudRepository repository ) {
		this.entityConfiguration = entityConfiguration;
		this.repository = repository;
	}

	@Override
	public Collection<SelectOption> generateOptions( EntityMessageCodeResolver codeResolver ) {
		// todo: support sorting
		return populateOptionsList( findEntities() );
	}

	@SuppressWarnings("unchecked")
	protected Collection<SelectOption> populateOptionsList( Iterable entities ) {
		List<SelectOption> options = new ArrayList<>();
		for ( Object entity : entities ) {
			SelectOption option = new SelectOption();
			option.setLabel( entityConfiguration.getLabel( entity ) );
			option.setValue( entityConfiguration.getId( entity ).toString() );
			option.setRawValue( entity );

			options.add( option );
		}
		return options;
	}

	protected Iterable findEntities() {
		return repository.findAll();
	}
}
