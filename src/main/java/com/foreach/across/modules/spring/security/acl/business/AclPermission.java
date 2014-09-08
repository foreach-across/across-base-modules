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
package com.foreach.across.modules.spring.security.acl.business;

import org.springframework.security.acls.domain.AbstractPermission;

/**
 * Redefined instance of {@link org.springframework.security.acls.domain.BasePermission} to avoid
 * confusion with the Permission from UserModule.
 * }
 *
 * @author Arne Vandamme
 */
public class AclPermission extends AbstractPermission
{
	public static final AclPermission READ = new AclPermission( 1 << 0, 'R' ); // 1
	public static final AclPermission WRITE = new AclPermission( 1 << 1, 'W' ); // 2
	public static final AclPermission CREATE = new AclPermission( 1 << 2, 'C' ); // 4
	public static final AclPermission DELETE = new AclPermission( 1 << 3, 'D' ); // 8
	public static final AclPermission ADMINISTRATION = new AclPermission( 1 << 4, 'A' ); // 16

	protected AclPermission( int mask ) {
		super( mask );
	}

	protected AclPermission( int mask, char code ) {
		super( mask, code );
	}
}
