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

/**
 * @author Arne Vandamme
 */
public interface AclAuthorities
{
	/**
	 * Global authority that allows the authentication to take ownership of an ACL.
	 */
	String TAKE_OWNERSHIP = "acl take ownership";

	/**
	 * Global authority that allows an ACL to be modified (add/delete aces).
	 */
	String MODIFY_ACL = "acl modify";

	/**
	 * Global authority that allows the auditing settings of an ACL to be modified.
	 */
	String AUDIT_ACL = "acl audit";

}
