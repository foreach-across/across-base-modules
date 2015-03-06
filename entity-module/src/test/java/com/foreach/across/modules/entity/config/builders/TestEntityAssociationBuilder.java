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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
public class TestEntityAssociationBuilder
{
	private EntitiesConfigurationBuilder entities;
	private MutableEntityRegistry entityRegistry;
	private AutowireCapableBeanFactory beanFactory;

	@Before
	public void before() {
		entities = new EntitiesConfigurationBuilder();
		entityRegistry = new EntityRegistryImpl();
		beanFactory = mock( AutowireCapableBeanFactory.class );
	}

	@Test
	public void manuallyCreateEntityConfiguration() {
		entities.entity( OtherEntity.class );

		entities.entity( SomeEntity.class )
		        .association( "other" )
		        .targetEntityType( OtherEntity.class )
		        .hide();

		entities.apply( entityRegistry, beanFactory );

		EntityConfiguration config = entityRegistry.getEntityConfiguration( SomeEntity.class );
		assertNotNull( config );

		EntityAssociation association = config.association( "other" );
		assertNotNull( association );
		assertTrue( association.isHidden() );
	}

	static class SomeEntity
	{

	}

	static class OtherEntity
	{

	}
}
