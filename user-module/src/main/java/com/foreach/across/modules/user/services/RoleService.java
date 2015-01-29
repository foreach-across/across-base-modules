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

import com.foreach.across.modules.user.business.Role;

import java.util.Collection;

public interface RoleService
{
	/**
	 * Ensures a Role with the specified permissions exists.  The name is the unique key of the role.
	 *
	 * @param authority   Unique authority of the Role entity.
	 * @param name        Descriptive name of the Role.
	 * @param permissions Permission names to apply to the role.
	 * @return Role instance that was created or updated.
	 */
	Role defineRole( String authority, String name, Collection<String> permissions );

	/**
	 * Ensures the given Role exists based on the unique name.
	 *
	 * @param role Role entity that should exist.
	 */
	Role defineRole( Role role );

	/**
	 * Get all defined Roles.
	 *
	 * @return Collection of Role entities.
	 */
	Collection<Role> getRoles();

	/**
	 * Get the Role entity with the given authority.
	 *
	 * @param authority Unique authority of the Role.
	 * @return Role entity or null;
	 */
	Role getRole( String authority );

	/**
	 * Save the given Role entity.
	 *
	 * @param role Entity to save.
	 */
	Role save( Role role );

	/**
	 * Delete the given Role entity.
	 *
	 * @param role Entity to delete.
	 */
	void delete( Role role );
}
