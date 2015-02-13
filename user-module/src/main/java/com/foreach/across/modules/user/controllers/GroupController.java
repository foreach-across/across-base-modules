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
package com.foreach.across.modules.user.controllers;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AdminWebController
@RequestMapping(GroupController.PATH)
public class GroupController
{
	public static final String PATH = "/groups";

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private GroupService groupService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private MenuFactory menuFactory;

//	@Event
//	protected void registerGroupsTab( EntityAdminMenuEvent<GroupedPrincipal> menu ) {
//		if ( menu.isForUpdate() ) {
//			menu.builder().item( "groups", "Groups",
//			                     "/entities/" + ( menu.getEntityType().getSimpleName().toLowerCase() ) + "/" + menu
//					                     .getEntity().getId() + "/groups" );
//		}
//	}

	@RequestMapping
	public String listGroups( Model model ) {
		model.addAttribute( "groups", groupService.getGroups() );

		return "th/user/groups/list";
	}

	@RequestMapping("/create")
	public String createEntity( Model model ) {
		model.addAttribute( "entityMenu", menuFactory.buildMenu( new EntityAdminMenu<>( Group.class ) ) );
		model.addAttribute( "existing", false );
		model.addAttribute( "group", new Group() );
		model.addAttribute( "roles", roleService.getRoles() );

		return "th/user/groups/edit";
	}

	@RequestMapping("/{groupId}")
	public String editEntity( @PathVariable("groupId") Group group,
	                          AdminMenu adminMenu,
	                          Model model ) {
		model.addAttribute( "entityMenu", menuFactory.buildMenu( new EntityAdminMenu<>( Group.class, group ) ) );

		adminMenu.getLowestSelectedItem().addItem( "/selectedGroup", group.getName() ).setSelected( true );

		model.addAttribute( "existing", true );
		model.addAttribute( "group", group.toDto() );
		model.addAttribute( "roles", roleService.getRoles() );

		return "th/user/groups/edit";
	}

	@RequestMapping(value = { "/create", "/{groupId}" }, method = RequestMethod.POST)
	public String saveEntity( @ModelAttribute("group") Group groupDto, RedirectAttributes re ) {
		Group existing = null;

		if ( !groupDto.isNew() ) {
			existing = groupService.getGroupById( groupDto.getId() );
		}

		groupService.save( groupDto );

		re.addAttribute( "groupId", groupDto.getId() );

		return adminWeb.redirect( "/groups/{groupId}" );
	}
}
