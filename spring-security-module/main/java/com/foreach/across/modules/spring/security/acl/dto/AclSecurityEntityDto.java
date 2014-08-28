package com.foreach.across.modules.spring.security.acl.dto;

import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;

/**
 * @author Arne Vandamme
 */
public class AclSecurityEntityDto extends IdBasedEntityDto<AclSecurityEntity>
{
	private String name;
	private AclSecurityEntity parent;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public AclSecurityEntity getParent() {
		return parent;
	}

	public void setParent( AclSecurityEntity parent ) {
		this.parent = parent;
	}
}
