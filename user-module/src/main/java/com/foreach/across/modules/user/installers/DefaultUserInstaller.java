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
package com.foreach.across.modules.user.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

@Installer(description = "Installs the default permissions, roles and user", version = 5,
           phase = InstallerPhase.AfterModuleBootstrap)
public class DefaultUserInstaller
{
	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@InstallerMethod
	public void install() {
		createSystemAccount();
		createPermissionGroups();
		createPermissionsAndRoles();
		createUser();
	}

	private void createSystemAccount() {
		MachinePrincipal machine = machinePrincipalService.getMachinePrincipalByName( "system" );

		if ( machine == null ) {
			MachinePrincipal dto = new MachinePrincipal();
			dto.setName( "system" );

			machinePrincipalService.save( dto );
		}
	}

	private void createPermissionGroups() {
		PermissionGroup group = permissionService.getPermissionGroup( UserModule.NAME );

		if ( group == null ) {
			PermissionGroup dto = new PermissionGroup();
			dto.setName( UserModule.NAME );
			dto.setTitle( "Module: " + UserModule.NAME );
			dto.setDescription( "Basic user and user management related permissions." );

			permissionService.saveGroup( dto );
		}
	}

	private void createPermissionsAndRoles() {
		permissionService.definePermission( "access administration",
		                                    "User can perform one or more administrative actions.  Usually this means the user can access the administration interface.",
		                                    UserModule.NAME );

		permissionService.definePermission( "manage users", "Manage user accounts", UserModule.NAME );
		permissionService.definePermission( "manage groups", "Manage groups", UserModule.NAME );
		permissionService.definePermission( "manage user roles", "Manage user roles", UserModule.NAME );

		Role adminRole = roleService.getRole( "ROLE_ADMIN" );
		if ( adminRole == null ) {
			roleService.defineRole( "ROLE_ADMIN", "Administrator",
			                        Arrays.asList( "access administration", "manage users", "manage groups",
			                                       "manage user roles" ) );
		}
		else {
			adminRole.addPermission( "manage groups" );
			roleService.save( adminRole );
		}

		Role managerRole = roleService.getRole( "ROLE_MANAGER" );
		if ( managerRole == null ) {
			roleService.defineRole( "ROLE_MANAGER", "Manager", Arrays.asList( "access administration", "manage users",
			                                                                  "manage groups" ) );
		}
		else {
			managerRole.addPermission( "manage groups" );
			roleService.save( managerRole );
		}
	}

	private void createUser() {
		User existing = userService.getUserByUsername( "admin" );

		if ( existing == null ) {
			User user = new User();
			user.setUsername( "admin" );
			user.setPassword( "admin" );
			user.setFirstName( "" );
			user.setLastName( "" );
			user.setDisplayName( "Root administrator" );
			user.setEmail( "admin@localhost" );
			user.setDeleted( false );
			user.setEmailConfirmed( true );

			HashSet<Role> roles = new HashSet<>();
			roles.add( roleService.getRole( "ROLE_ADMIN" ) );

			user.setRoles( roles );

			userService.save( user );
		}
	}
}
