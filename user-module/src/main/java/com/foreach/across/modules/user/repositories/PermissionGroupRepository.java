package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.user.business.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionGroupRepository
		extends JpaRepository<PermissionGroup, Long>
{
	PermissionGroup findByName( String name );
}
