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
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.Client;
import com.foreach.across.modules.entity.testmodules.springdata.Company;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEntityConfigurationBuilder
{
	private EntitiesConfigurationBuilder builders;
	private MutableEntityRegistry entityRegistry;

	private EntityConfigurationBuilder builder;
	private MutableEntityConfiguration client, company;

	@Before
	public void reset() {
		builders = new EntitiesConfigurationBuilder();

		entityRegistry = new EntityRegistryImpl();

		client = mock( MutableEntityConfiguration.class );
		when( client.getEntityType() ).thenReturn( Client.class );
		when( client.getName() ).thenReturn( "client" );

		company = mock( MutableEntityConfiguration.class );
		when( company.getEntityType() ).thenReturn( Company.class );
		when( company.getName() ).thenReturn( "company" );

		entityRegistry.register( client );
		entityRegistry.register( company );

		builder = builders.entity( Client.class );
	}
}
