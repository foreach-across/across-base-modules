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
package com.foreach.across.modules.entity.views.support;

import com.foreach.across.modules.entity.registrars.repository.TestRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestEntityMessages
{
	@Autowired
	private EntityRegistry entityRegistry;

	private EntityMessageCodeResolver messageCodeResolver;

	@Before
	public void fetchResolver() {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Client.class );

		messageCodeResolver = entityConfiguration.getEntityMessageCodeResolver();
		assertNotNull( messageCodeResolver );

		LocaleContextHolder.setLocale( Locale.ENGLISH );
	}

	@Test
	public void actionMessages() {
		EntityMessages messages = new EntityMessages( messageCodeResolver );

		assertEquals( "Create a new client", messages.createAction() );
		assertEquals( "Modify client", messages.updateAction() );
		assertEquals( "Delete client", messages.deleteAction() );
		assertEquals( "View client details", messages.viewAction() );
	}

	@Test
	public void listViewMessages() {
		ListViewEntityMessages messages = new ListViewEntityMessages( messageCodeResolver );

		Page page = mock( Page.class );
		when( page.getNumber() ).thenReturn( 2 );
		when( page.getTotalPages() ).thenReturn( 6 );

		assertEquals( "Showing page 3 of 6", messages.pagerText( page ) );
		assertEquals( "next page", messages.nextPage( page ) );
		assertEquals( "previous page", messages.previousPage( page ) );

		when( page.getTotalElements() ).thenReturn( 0L );
		assertEquals( "No clients found.", messages.resultsFound( page ) );

		when( page.getTotalElements() ).thenReturn( 1L );
		assertEquals( "1 client found.", messages.resultsFound( page ) );

		when( page.getTotalElements() ).thenReturn( 115L );
		assertEquals( "115 clients found.", messages.resultsFound( page ) );
	}
}
