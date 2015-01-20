package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.controllers.EntityController;
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
		PrefixingPathContext ctx = new PrefixingPathContext( "/secure" );

		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( anyObject() ) ).thenReturn( 10001 );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		EntityLinkBuilder url = new EntityLinkBuilder( ctx, EntityController.PATH, entityConfiguration );

		assertEquals( "/secure/entities/basicPrincipal", url.overview() );
		assertEquals( "/secure/entities/basicPrincipal/create", url.create() );
		assertEquals( "/secure/entities/basicPrincipal/10001", url.view( "someEntity" ) );
		assertEquals( "/secure/entities/basicPrincipal/10001/update", url.update( "someEntity" ) );
		assertEquals( "/secure/entities/basicPrincipal/10001/delete", url.delete( "someEntity" ) );
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

		EntityLinkBuilder url = new EntityLinkBuilder( ctx, EntityController.PATH, entityConfiguration );

		assertEquals( "/secure/entities/basicPrincipal", url.overview() );
		assertEquals( "/secure/entities/basicPrincipal/create", url.create() );
		assertEquals( "/secure/entities/basicPrincipal/someStringId", url.view( "someEntity" ) );
		assertEquals( "/secure/entities/basicPrincipal/someStringId/update", url.update( "someEntity" ) );
		assertEquals( "/secure/entities/basicPrincipal/someStringId/delete", url.delete( "someEntity" ) );
	}
}
