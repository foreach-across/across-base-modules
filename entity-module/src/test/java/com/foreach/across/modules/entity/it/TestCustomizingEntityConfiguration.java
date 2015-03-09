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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.web.EntityConfigurationLinkBuilder;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestCustomizingEntityConfiguration.Config.class)
public class TestCustomizingEntityConfiguration
{
	@Autowired
	private EntityRegistry entityRegistry;

	private EntityConfiguration configuration;

	@Before
	public void retrieveEntityConfiguration() {
		configuration = entityRegistry.getEntityConfiguration( Client.class );
	}

	@Test
	public void clientShouldBeRegistered() {
//		assertEquals( 3, entityRegistry.getEntities().size() );
		assertTrue( entityRegistry.contains( Client.class ) );

		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertNotNull( configuration );
	}

	@Test
	public void attributesShouldBeSet() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );

		assertNotNull( configuration.getAttribute( EntityLinkBuilder.class ) );
	}

	@Test
	public void customPropertiesOnEntity() {
		EntityPropertyRegistry registry = configuration.getPropertyRegistry();
		assertNotNull( registry );

		EntityPropertyDescriptor descriptor = registry.getProperty( "someprop" );
		assertNotNull( descriptor );
		assertEquals( "someprop", descriptor.getName() );
		assertEquals( "Some property", descriptor.getDisplayName() );
		assertTrue( descriptor.getValueFetcher() instanceof SpelValueFetcher );
	}

	@Test
	public void crudListViewShouldBeModified() {
		EntityViewFactory viewFactory = configuration.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( viewFactory );
	}

	@Test
	public void extraViewsShouldExist() {
		assertTrue( configuration.hasView( "some-extra-view" ) );

		EntityViewFactory viewFactory = configuration.getViewFactory( "some-extra-view" );
		assertNotNull( viewFactory );
		assertTrue( viewFactory instanceof SimpleEntityViewFactorySupport );

		ConfigurablePropertiesEntityViewFactorySupport common = (ConfigurablePropertiesEntityViewFactorySupport) viewFactory;
		assertEquals( "th/someTemplate", common.getTemplate() );

		EntityPropertyDescriptor calculated = common.getPropertyRegistry().getProperty( "calculated" );
		assertNotNull( calculated );
		assertEquals( "Calculated", calculated.getDisplayName() );
		assertTrue( calculated.getValueFetcher() instanceof SpelValueFetcher );

		EntityPropertyDescriptor groupMembership = common.getPropertyRegistry().getProperty( "group-membership" );
		assertNotNull( groupMembership );
		assertEquals( "Group membership", groupMembership.getDisplayName() );
		assertTrue( groupMembership.getValueFetcher() instanceof SpelValueFetcher );

		assertTrue( configuration.hasView( "some-other-view" ) );
		assertNotNull( configuration.getViewFactory( "some-other-view" ) );
	}

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityModule() );
			context.addModule( new AdminWebModule() );
			context.addModule( new EntityModule() );

			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create" );
			context.addModule( hibernateModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.setExposeFilter( new ClassBeanFilter( ClientRepository.class ) );
			context.addModule( springDataJpaModule );

			EmptyAcrossModule customModule = new EmptyAcrossModule( "customizingModule" );
			customModule.addApplicationContextConfigurer( ModuleConfig.class );
			context.addModule( customModule );
		}
	}

	@Configuration
	protected static class ModuleConfig implements EntityConfigurer
	{
		@Override
		@SuppressWarnings( "unchecked" )
		public void configure( EntitiesConfigurationBuilder configuration ) {
			configuration.attribute( EntityLinkBuilder.class, mock( EntityConfigurationLinkBuilder.class ) );

			configuration.entity( Client.class )
			             .properties()
			             .property( "someprop" ).displayName( "Some property" ).spelValueFetcher( "'fixed'" )
			             .and().and()
			             .view( "some-extra-view" )
			             .template( "th/someTemplate" )
			             .properties()
			             .property( "calculated" ).displayName( "Calculated" ).and()
			             .property( "group-membership" ).displayName( "Group membership").spelValueFetcher( "groups.size()" ).and()
			             .and()
			             .and()
			             .view( "some-other-view" ).factory( mock( ConfigurablePropertiesEntityViewFactorySupport.class ) );
		}
	}
}
