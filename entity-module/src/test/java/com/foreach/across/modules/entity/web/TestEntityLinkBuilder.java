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

import com.foreach.across.modules.entity.controllers.entity.EntityListController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.web.context.PrefixingPathContext;
import org.junit.Test;

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
		when( model.getId( anyObject() ) ).thenReturn( 10001 );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		EntityLinkBuilder url = new EntityLinkBuilder( EntityListController.PATH, entityConfiguration );

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
		PrefixingPathContext ctx = new PrefixingPathContext( "/secure" );

		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( anyObject() ) ).thenReturn( "someStringId" );
		when( model.getIdType() ).thenReturn( String.class );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		EntityLinkBuilder url = new EntityLinkBuilder( EntityListController.PATH, entityConfiguration );

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

		EntityConfiguration association = mock( EntityConfiguration.class );
		when( association.getName() ).thenReturn( "user" );

		EntityModel parentModel = mock( EntityModel.class );
		when( parentModel.getId( "someEntity" ) ).thenReturn( 10001 );
		when( parentConfig.getEntityModel() ).thenReturn( parentModel );

		EntityModel associationModel = mock( EntityModel.class );
		when( associationModel.getId( anyObject() ) ).thenReturn( 123 );
		when( association.getEntityModel() ).thenReturn( associationModel );

		EntityLinkBuilder parent = new EntityLinkBuilder( EntityListController.PATH, parentConfig );
		EntityLinkBuilder url = new EntityLinkBuilder( EntityListController.PATH, association );
		url.setAssociationsPath( "{0}/{1}/others/{2}" );

		EntityLinkBuilder scoped = url.asAssociationFor( parent, "someEntity" );

		assertEquals( "/entities/user", url.overview() );
		assertEquals( "/entities/user/create", url.create() );
		assertEquals( "/entities/user/123", url.view( "someEntity" ) );
		assertEquals( "/entities/user/123/update", url.update( "someEntity" ) );
		assertEquals( "/entities/user/123/delete", url.delete( "someEntity" ) );
		assertEquals( "/entities/user/others/123", url.associations( "someEntity" ) );

		assertEquals( "/entities/basicPrincipal/10001/associations/user", scoped.overview() );
		assertEquals( "/entities/basicPrincipal/10001/associations/user/create", scoped.create() );
		assertEquals( "/entities/basicPrincipal/10001/associations/user/123", scoped.view( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations/user/123/update", scoped.update( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations/user/123/delete", scoped.delete( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations/user/others/123", scoped.associations( "someEntity" ) );
	}
}
