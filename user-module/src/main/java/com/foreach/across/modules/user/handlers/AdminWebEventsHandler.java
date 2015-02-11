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
package com.foreach.across.modules.user.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.events.AdminWebUrlRegistry;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.controllers.GroupController;
import com.foreach.across.modules.user.controllers.RoleController;
import com.foreach.across.modules.user.controllers.UserController;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@AcrossEventHandler
public class AdminWebEventsHandler
{
	@Autowired
	private CurrentSecurityPrincipalProxy currentPrincipal;

	@Event
	public void secureUrls( AdminWebUrlRegistry urls ) {
		urls.match( UserController.PATH, UserController.PATH + "/*" ).hasAuthority( "manage users" );
		urls.match( RoleController.PATH, RoleController.PATH + "/*" ).hasAuthority( "manage user roles" );
		urls.match( GroupController.PATH, GroupController.PATH + "/*" ).hasAuthority( "manage groups" );
	}

	@Event
	public void registerMenu( AdminMenuEvent adminMenuEvent ) {
		PathBasedMenuBuilder builder = adminMenuEvent.builder();
		builder.group( "/users", "User management" );

		if ( currentPrincipal.hasAuthority( "manage users" ) ) {
			builder.item( "/users/users", "Users", UserController.PATH ).order( 1 ).and()
			       .item( "/users/users/create", "Create a new user", UserController.PATH + "/create" );
		}

		if ( currentPrincipal.hasAuthority( "manage user roles" ) ) {
			builder
					.item( "/users/roles", "Roles", RoleController.PATH ).order( 3 ).and()
					.item( "/users/roles/create", "Create a new role", RoleController.PATH + "/create" );
		}

		if ( currentPrincipal.hasAuthority( "manage groups" ) ) {
			builder
					.item( "/users/groups", "Groups", GroupController.PATH ).order( 2 ).and()
					.item( "/users/groups/create", "Create a new group", GroupController.PATH + "/create" );
		}
	}

	/*
	@Event
	public void userMenu( EntityAdminMenuEvent<User> menuEvent ) {
		PathBasedMenuBuilder builder = menuEvent.builder();

		if ( menuEvent.isExisting() ) {
			builder.item( "/users/" + menuEvent.getEntity().getId(), "Properties" );
		}
		else {
			builder.item( "/users/create", "Properties" );
		}
	}

	@Event
	public void groupMenu( EntityAdminMenuEvent<Group> menuEvent ) {
		PathBasedMenuBuilder builder = menuEvent.builder();

		if ( menuEvent.isExisting() ) {
			builder
					.item( "properties", "Properties", "/groups/" + menuEvent.getEntity().getId() ).order( -10 ).and()
					.item( "members", "Members", "/groups/" + menuEvent.getEntity().getId() + "/members" ).order( -9 );
		}
		else {
			builder.item( "properties", "Properties", "/groups/create" );
		}
	}
	*/
}
