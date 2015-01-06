package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.repositories.Undeletable;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.Group;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TestGroupAclInterceptor.Config.class)
@DirtiesContext
public class TestGroupAclInterceptor
{
	@Autowired
	private GroupAclInterceptor groupAclInterceptor;

	@Autowired
	private UserModuleSettings userModuleSettings;

	@Autowired
	private AclSecurityService aclSecurityService;

	@Autowired
	private AclSecurityEntityService aclSecurityEntityService;

	@Before
	public void resetMocks() {
		reset( userModuleSettings, aclSecurityService, aclSecurityEntityService );
	}

	@Test
	public void testAclCreatedWithConfigEnabled() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		AclSecurityEntity groups = new AclSecurityEntity();
		groups.setId( -1L );
		groups.setName( "groups" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( true );
		when( aclSecurityEntityService.getSecurityEntityByName( "groups" ) ).thenReturn( groups );

		groupAclInterceptor.afterCreate( group );

		verify( aclSecurityService ).createAclWithParent( group, groups );
		verifyNoMoreInteractions( aclSecurityService );
	}

	@Test
	public void testAclNotCreatedWithConfigDisabled() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( false );

		groupAclInterceptor.afterCreate( group );

		verifyZeroInteractions( aclSecurityService, aclSecurityEntityService );
	}

	@Test
	public void testAclDeleted() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( true );

		groupAclInterceptor.beforeDelete( group, (group instanceof Undeletable) );

		verify( aclSecurityService ).deleteAcl( group, true );
		verifyNoMoreInteractions( aclSecurityService );
	}

	@Test
	public void testAclNotDeletedWithConfigDisabled() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( false );

		groupAclInterceptor.afterDelete( group, (group instanceof Undeletable) );

		verifyZeroInteractions( aclSecurityService, aclSecurityEntityService );
	}

	@Configuration
	static class Config
	{
		@Bean
		public GroupAclInterceptor groupAclInterceptor() {
			return new GroupAclInterceptor();
		}
	}
}