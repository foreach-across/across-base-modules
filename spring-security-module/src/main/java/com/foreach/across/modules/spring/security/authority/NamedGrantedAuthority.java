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

package com.foreach.across.modules.spring.security.authority;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * <p>
 *     Simple implementation of {@link org.springframework.security.core.GrantedAuthority} where the
 *     entire authority is represented by a unique name (String).  This is a common case.
 * </p>
 * <p>
 *     This implementation differs from {@link org.springframework.security.core.authority.SimpleGrantedAuthority}
 *     in that it is more lenient when it comes to equals.  It considers any
 *     {@link org.springframework.security.core.GrantedAuthority} implementation equal if it returns the same
 *     value for {@link #getAuthority()}.
 * </p>
 * @author Arne Vandamme
 */
@Deprecated
public class NamedGrantedAuthority implements GrantedAuthority
{
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final String authority;

	public NamedGrantedAuthority( String authority ) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || !( o instanceof GrantedAuthority ) ) {
			return false;
		}

		return StringUtils.equals( authority, ( (GrantedAuthority) o ).getAuthority() );
	}

	@Override
	public int hashCode() {
		return authority != null ? authority.hashCode() : 0;
	}

}
