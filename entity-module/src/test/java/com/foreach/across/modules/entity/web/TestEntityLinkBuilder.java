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
package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.controllers.EntityControllerAttributes;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEntityLinkBuilder
{
	@Test
	@SuppressWarnings("unchecked")
	public void generatedPathsWithNumberId() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( anyObject() ) ).thenReturn( new BigDecimal( "100.01" ) );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( new BigDecimal( "100.01" ), String.class ) ).thenReturn( "10001" );

		EntityLinkBuilder url = new EntityConfigurationLinkBuilder(
				EntityControllerAttributes.ROOT_PATH, entityConfiguration, conversionService
		);

		assertEquals( "/entities/basicPrincipal", url.overview() );
		assertEquals( "/entities/basicPrincipal/create", url.create() );
		assertEquals( "/entities/basicPrincipal/10001", url.view( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/update", url.update( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/delete", url.delete( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations", url.associations( "someEntity" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void generatedPathsWithStringId() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( anyObject() ) ).thenReturn( "someStringId" );
		when( model.getIdType() ).thenReturn( String.class );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( "someStringId", String.class ) ).thenReturn( "someStringId" );

		EntityLinkBuilder url =
				new EntityConfigurationLinkBuilder( EntityControllerAttributes.ROOT_PATH, entityConfiguration,
				                                    conversionService );

		assertEquals( "/entities/basicPrincipal", url.overview() );
		assertEquals( "/entities/basicPrincipal/create", url.create() );
		assertEquals( "/entities/basicPrincipal/someStringId", url.view( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/someStringId/update", url.update( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/someStringId/delete", url.delete( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/someStringId/associations", url.associations( "someEntity" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void asAssociationLinkBuilders() {
		EntityConfiguration parentConfig = mock( EntityConfiguration.class );
		when( parentConfig.getName() ).thenReturn( "basicPrincipal" );

		EntityConfiguration associatedConfig = mock( EntityConfiguration.class );
		when( associatedConfig.getName() ).thenReturn( "user" );

		EntityModel parentModel = mock( EntityModel.class );
		when( parentModel.getId( "someEntity" ) ).thenReturn( 10001 );
		when( parentConfig.getEntityModel() ).thenReturn( parentModel );

		EntityModel associationModel = mock( EntityModel.class );
		when( associationModel.getId( anyObject() ) ).thenReturn( 123 );
		when( associatedConfig.getEntityModel() ).thenReturn( associationModel );

		EntityAssociation association = mock( EntityAssociation.class );
		when( association.getName() ).thenReturn( "company.user" );
		when( association.getSourceEntityConfiguration() ).thenReturn( parentConfig );
		when( association.getTargetEntityConfiguration() ).thenReturn( associatedConfig );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( 10001, String.class ) ).thenReturn( "10001" );
		when( conversionService.convert( 123, String.class ) ).thenReturn( "123" );

		EntityLinkBuilder parent =
				new EntityConfigurationLinkBuilder( EntityControllerAttributes.ROOT_PATH, parentConfig,
				                                    conversionService );
		EntityAssociationLinkBuilder url = new EntityAssociationLinkBuilder( association, conversionService );
		url.setAssociationsPath( "{0}/{1}/others/{2}" );

		EntityLinkBuilder scoped = url.asAssociationFor( parent, "someEntity" );

		assertEquals( "/company.user", url.overview() );
		assertEquals( "/company.user/create", url.create() );
		assertEquals( "/company.user/123", url.view( "someEntity" ) );
		assertEquals( "/company.user/123/update", url.update( "someEntity" ) );
		assertEquals( "/company.user/123/delete", url.delete( "someEntity" ) );
		assertEquals( "/company.user/others/123", url.associations( "someEntity" ) );

		assertEquals( "/entities/basicPrincipal/10001/associations/company.user", scoped.overview() );
		assertEquals( "/entities/basicPrincipal/10001/associations/company.user/create", scoped.create() );
		assertEquals( "/entities/basicPrincipal/10001/associations/company.user/123", scoped.view( "assoc" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations/company.user/123/update", scoped.update( "assoc" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations/company.user/123/delete", scoped.delete( "assoc" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations/company.user/others/123",
		              scoped.associations( "assoc" ) );
	}
}
