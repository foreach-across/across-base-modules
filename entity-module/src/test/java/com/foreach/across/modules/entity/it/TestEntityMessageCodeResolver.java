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
package com.foreach.across.modules.entity.it;

import com.foreach.across.modules.entity.registrars.repository.TestRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestEntityMessageCodeResolver
{
	private static final Locale NL = Locale.forLanguageTag( "nl-BE" );

	@Autowired
	private EntityRegistry entityRegistry;

	private EntityConfiguration<Client> entityConfiguration;
	private EntityMessageCodeResolver messages;

	@Before
	public void fetchResolver() {
		entityConfiguration = entityRegistry.getEntityConfiguration( Client.class );
		assertNotNull( entityConfiguration );

		messages = entityConfiguration.getEntityMessageCodeResolver();
		assertNotNull( messages );

		LocaleContextHolder.setLocale( Locale.ENGLISH );
	}

	@Test
	public void noDefaultValue() {
		assertEquals(
				"SpringDataJpaModule.entities.client.someCode",
				messages.getMessage( "someCode", null )
		);
	}

	@Test
	public void defaultEntityNames() {
		assertEquals( "Client", messages.getNameSingular() );
		assertEquals( "client", messages.getNameSingularInline() );
		assertEquals( "Clients", messages.getNamePlural() );
		assertEquals( "clients", messages.getNamePluralInline() );
	}

	@Test
	public void entityNamesWithLocale() {
		assertEquals( "Klant", messages.getNameSingular( NL ) );
		assertEquals( "klant", messages.getNameSingularInline( NL ) );
		assertEquals( "Klanten", messages.getNamePlural( NL ) );
		assertEquals( "klanten", messages.getNamePluralInline( NL ) );
	}

	@Test
	public void entityNamesInPrefixedContext() {
		EntityMessageCodeResolver prefixed = messages.prefixedResolver( "entityViews.listView", "entityViews" );

		assertEquals( "Client", prefixed.getNameSingular() );
		assertEquals( "client member", prefixed.getNameSingularInline() );
		assertEquals( "Clients", prefixed.getNamePlural() );
		assertEquals( "client members", prefixed.getNamePluralInline() );

		assertEquals( "Klant", prefixed.getNameSingular( NL ) );
		assertEquals( "klanten lid", prefixed.getNameSingularInline( NL ) );
		assertEquals( "Klanten", prefixed.getNamePlural( NL ) );
		assertEquals( "client members", prefixed.getNamePluralInline( NL ) );
	}

	@Test
	public void propertyDescriptors() {
		EntityPropertyDescriptor nameProperty = entityConfiguration.getPropertyRegistry().getProperty( "name" );
		EntityPropertyDescriptor idProperty = entityConfiguration.getPropertyRegistry().getProperty( "id" );

		assertEquals( "Name", messages.getPropertyDisplayName( nameProperty ) );
		assertEquals( "Naam", messages.getPropertyDisplayName( nameProperty, NL ) );
		assertEquals( "Identity", messages.getPropertyDisplayName( idProperty, NL ) );

		EntityMessageCodeResolver prefixed = messages.prefixedResolver( "entityViews.listView", "entityViews" );
		assertEquals( "Name", prefixed.getPropertyDisplayName( nameProperty ) );
		assertEquals( "Naam", prefixed.getPropertyDisplayName( nameProperty, NL ) );
		assertEquals( "Identiteit", prefixed.getPropertyDisplayName( idProperty, NL ) );
	}

	@Test
	public void customMessages() {
		String createMessage = messages.getMessage( "actions.create",
		                                            new Object[] { messages.getNameSingularInline() },
		                                            "Default for create" );
		assertEquals( "Default for create", createMessage );

		createMessage = messages.getMessageWithFallback( "actions.create",
		                                                 new Object[] { "", messages.getNameSingularInline() },
		                                                 "Default for create" );
		assertEquals( "Create a new client", createMessage );

		createMessage = messages.getMessageWithFallback( "actions.create",
		                                                 new Object[] { messages.getNameSingularInline( NL ) },
		                                                 "Default for create",
		                                                 NL );
		assertEquals( "Een nieuwe klant aanmaken", createMessage );

		LocaleContextHolder.setLocale( NL );
		createMessage = messages.getMessageWithFallback( "actions.create",
		                                                 new Object[] { messages.getNameSingularInline() },
		                                                 "Default for create"
		);
		assertEquals( "Een nieuwe klant aanmaken", createMessage );

		EntityMessageCodeResolver prefixed = messages.prefixedResolver( "entityViews.listView", "entityViews" );
		createMessage = prefixed.getMessageWithFallback( "actions.create",
		                                                 new Object[] { messages.getNameSingularInline() },
		                                                 "Default for create"
		);
		assertEquals( "Eentje aanmaken, nen kalant", createMessage );
	}

	@Test
	public void staticGenerationMultiple() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities" };
		String[] subCollections = new String[] { "views.listView", "views" };
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void emptyRootCollection() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities", "" };
		String[] subCollections = new String[] { "views.listView", "views" };
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName",
						"views.listView.properties.displayName",
						"views.properties.displayName",
						"properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationRootCollectionOnly() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities" };
		String[] subCollections = new String[0];
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationSingle() {
		String[] rootCollections = new String[] { "UserModule.entities.user" };
		String[] subCollections = new String[] { "views.listView" };
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.properties.displayName"
				},
				generated
		);
	}
}
