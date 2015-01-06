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

import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import com.foreach.across.modules.user.repositories.RoleRepository;
import liquibase.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Service
public class RoleServiceImpl implements RoleService
{
	private static final Logger LOG = LoggerFactory.getLogger( RoleServiceImpl.class );

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Role defineRole( String name, String description, Collection<String> permissionNames ) {
		Role role = new Role( name, description );

		Set<Permission> permissions = new TreeSet<>();

		for ( String permissionName : permissionNames ) {
			Permission permission = permissionRepository.findByName( permissionName );
			Assert.notNull( permission, "Invalid permission: " + permissionName );

			permissions.add( permission );
		}

		role.setPermissions( permissions );

		return defineRole( role );
	}

	@Override
	public Role defineRole( Role role ) {
		Role existing = roleRepository.findByName( role.getName() );

		if ( existing != null ) {
			if ( existing.getPermissions().size() != role.getPermissions().size() ) {
				Collection<Permission> difference = CollectionUtils.disjunction( existing.getPermissions(),
				                                                                 role.getPermissions() );
				Collection<String> permissionNames = CollectionUtils.collect( difference,
				                                                              new Transformer<Permission, String>()
				                                                              {
					                                                              @Override
					                                                              public String transform( Permission input ) {
						                                                              return input.getName();
					                                                              }
				                                                              },
				                                                              new ArrayList<String>()
				);
				LOG.error(
						"Cannot redefine role '{}' because it would loose the permissions: '{}', you should .addPermission() instead",
						role,
						StringUtils.join( permissionNames, ", " ) );
			}

			return existing;
		}
		else {
			return roleRepository.save( role );
		}
	}

	@Override
	public Collection<Role> getRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role getRole( String name ) {
		return roleRepository.findByName( name );
	}

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Override
	public Role save( Role role ) {
		Set<Permission> actualPermissions = new TreeSet<>();

		for ( Permission permission : role.getPermissions() ) {
			Permission existing = permissionRepository.findByName( permission.getName() );

			if ( existing == null ) {
				throw new RuntimeException( "No permission defined with name: " + permission.getName() );
			}

			actualPermissions.add( existing );
		}

		role.setPermissions( actualPermissions );

		return roleRepository.save( role );
	}

	@Override
	public void delete( Role role ) {
		roleRepository.delete( role );
	}
}
