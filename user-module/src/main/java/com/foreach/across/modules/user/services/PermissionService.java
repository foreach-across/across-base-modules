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
package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;

import java.util.Collection;

public interface PermissionService
{
	/**
	 * Ensures a permission with the given name and description exists.  The name
	 * is the unique identifier of the permission.
	 *
	 * @param name        Unique name of the permission.
	 * @param description Description of the permission.
	 * @param group       Group for the permission.
	 * @return Permission instance that was created or updated.
	 */
	Permission definePermission( String name, String description, PermissionGroup group );

	/**
	 * Ensures a permission with the given name and description exists.  The name
	 * is the unique identifier of the permission.
	 *
	 * @param name        Unique name of the permission.
	 * @param description Description of the permission.
	 * @param groupName   Name of the permission group the permission should be linked to.
	 * @return Permission instance that was created or update
	 */
	Permission definePermission( String name, String description, String groupName );

	/**
	 * Ensures the given permission exists based on the unique name.
	 *
	 * @param permission Permission entity that should exist.
	 */
	Permission definePermission( Permission permission );

	/**
	 * Get all defined permission groups.
	 *
	 * @return Collection of PermissionGroup entities.
	 */
	Collection<PermissionGroup> getPermissionGroups();

	/**
	 * Get the PermissionGroup entity by name.
	 *
	 * @param name Unique name of the permission group.
	 * @return PermissionGroup entity of null.
	 */
	PermissionGroup getPermissionGroup( String name );

	/**
	 * Save the PermissionGroup entity.
	 *
	 * @param dto Entity to save.
	 * @return Persisted entity.
	 */
	PermissionGroup saveGroup( PermissionGroup dto );

	/**
	 * Delete the PermissionGroup entity.
	 *
	 * @param group Entity to delete.
	 */
	void deleteGroup( PermissionGroup group );

	/**
	 * Get all defined permissions.
	 *
	 * @return Collection of Permission entities.
	 */
	Collection<Permission> getPermissions();

	/**
	 * Get the Permission entity by name.
	 *
	 * @param name Unique name of the permission.
	 * @return Permission entity of null.
	 */
	Permission getPermission( String name );

	/**
	 * Save the Permission entity.
	 *
	 * @param permission Entity to save.
	 */
	Permission savePermission( Permission permission );

	/**
	 * Delete the Permission entity.
	 *
	 * @param permission Entity to delete.
	 */
	void deletePermission( Permission permission );
}
