package com.foreach.across.modules.it.user;

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.services.PermissionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITUserModule.Config.class)
public class ITPermissionService
{
	@Autowired
	private PermissionService permissionService;

	@Test
	public void crudPermissionGroup() {
		String groupName = RandomStringUtils.randomAlphanumeric( 50 );

		assertNull( permissionService.getPermissionGroup( groupName ) );

		PermissionGroup dto = new PermissionGroup();
		dto.setName( groupName );
		dto.setTitle( "my group title" );

		PermissionGroup created = permissionService.saveGroup( dto );
		assertNotNull( created );

		PermissionGroup fetched = permissionService.getPermissionGroup( groupName );
		assertEquals( created, fetched );

		assertTrue( permissionService.getPermissionGroups().contains( fetched ) );

		permissionService.deleteGroup( fetched );

		assertNull( permissionService.getPermissionGroup( groupName ) );
		assertFalse( permissionService.getPermissionGroups().contains( fetched ) );
	}

	@Test
	public void crudPermissions() {
		PermissionGroup userGroup = new PermissionGroup();
		userGroup.setName( "test-users" );
		userGroup.setTitle( "Test users" );

		permissionService.saveGroup( userGroup );

		Permission manageUsersDto = new Permission( "manage users 2" );
		manageUsersDto.setGroup( userGroup );
		Permission manageUsers = permissionService.savePermission( manageUsersDto );

		assertTrue( manageUsers.getId() > 0 );

		Permission manageGroupsDto = new Permission( "manage groups 2" );
		manageGroupsDto.setGroup( userGroup );
		Permission manageGroups = permissionService.savePermission( manageGroupsDto );

		assertTrue( manageGroups.getId() > 0 );

		Permission existing = permissionService.getPermission( "manage users 2" );
		assertEquals( manageUsersDto, existing );
		assertEquals( manageUsersDto.getId(), existing.getId() );

		permissionService.deletePermission( existing );

		existing = permissionService.getPermission( "manage users 2" );
		assertNull( existing );

		Permission existingManageGroups = permissionService.getPermission( "manage groups 2" );
		assertEquals( manageGroupsDto, existingManageGroups );
		assertEquals( manageGroupsDto.getId(), existingManageGroups.getId() );

		permissionService.deletePermission( existingManageGroups );

		existing = permissionService.getPermission( "manage groups 2" );
		assertNull( existing );
	}
}
