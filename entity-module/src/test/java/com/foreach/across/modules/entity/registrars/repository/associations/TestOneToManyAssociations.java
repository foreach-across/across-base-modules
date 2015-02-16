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
package com.foreach.across.modules.entity.registrars.repository.associations;

import com.foreach.across.modules.entity.registrars.repository.TestRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.ClientGroup;
import com.foreach.across.modules.entity.views.EntityListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Verifies that a @ManyToOne is registered as a @OneToMany on the source entity.
 * If entity Client refers to a single Company, then an association should be created on Company that represents
 * all clients linked to that Company.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestOneToManyAssociations
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void clientHasAssociationToClientGroups() {
		EntityConfiguration clientGroup = entityRegistry.getEntityConfiguration( ClientGroup.class );
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );

		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );
		assertEquals(
				"Association name should be source entity name joined with target property name",
				"client.groups", association.getName()
		);

		assertSame( client, association.getSourceEntityConfiguration() );
		assertSame( clientGroup, association.getTargetEntityConfiguration() );

		assertNotNull( "OneToMany should have both source and target property set", association.getSourceProperty() );
		assertNotNull( "OneToMany should have both source and target property set", association.getTargetProperty() );

		assertSame( client.getPropertyRegistry().getProperty( "groups" ), association.getSourceProperty() );
		assertEquals( "id.client", association.getTargetProperty().getName() );

		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}
}
