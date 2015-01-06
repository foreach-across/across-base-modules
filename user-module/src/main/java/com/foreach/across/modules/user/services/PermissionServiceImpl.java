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
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.repositories.PermissionGroupRepository;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PermissionServiceImpl implements PermissionService
{
	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private PermissionGroupRepository permissionGroupRepository;

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Override
	public Permission definePermission( String name, String description, String groupName ) {
		PermissionGroup group = getPermissionGroup( groupName );

		if ( group == null ) {
			PermissionGroup dto = new PermissionGroup();
			dto.setName( groupName );
			dto.setTitle( groupName );

			group = saveGroup( dto );
		}

		return definePermission( name, description, group );
	}

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Override
	public Permission definePermission( String name, String description, PermissionGroup group ) {
		Permission permission = new Permission( name, description );
		permission.setGroup( group );

		definePermission( permission );

		return permission;
	}

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Override
	public Permission definePermission( Permission permissionDto ) {
		Permission existing = permissionRepository.findByName( permissionDto.getName() );

		if ( existing != null ) {
			existing.setName( permissionDto.getName() );
			existing.setDescription( permissionDto.getDescription() );
			existing.setGroup( permissionDto.getGroup() );

			existing = permissionRepository.save( existing );

			BeanUtils.copyProperties( existing, permissionDto );
		}
		else {
			existing = permissionRepository.save( permissionDto );
		}

		return existing;
	}

	@Override
	public Collection<PermissionGroup> getPermissionGroups() {
		return permissionGroupRepository.findAll();
	}

	@Override
	public PermissionGroup getPermissionGroup( String name ) {
		return permissionGroupRepository.findByName( name );
	}

	@Override
	public PermissionGroup saveGroup( PermissionGroup dto ) {
		PermissionGroup saved = permissionGroupRepository.save( dto );
		BeanUtils.copyProperties( saved, dto );

		return saved;
	}

	@Override
	public void deleteGroup( PermissionGroup group ) {
		permissionGroupRepository.delete( group );
	}

	@Override
	public Collection<Permission> getPermissions() {
		return permissionRepository.findAll();
	}

	@Override
	public Permission getPermission( String name ) {
		return permissionRepository.findByName( name );
	}

	@Override
	public Permission savePermission( Permission permission ) {
		Permission saved = permissionRepository.save( permission );
		BeanUtils.copyProperties( saved, permission );

		return saved;
	}

	@Override
	public void deletePermission( Permission permission ) {
		permissionRepository.delete( permission );
	}
}
