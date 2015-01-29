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
package com.foreach.across.modules.user.security;

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserProxyImpl implements CurrentUserProxy
{
	@Override
	public long getId() {
		return isAuthenticated() ? getUser().getId() : 0;
	}

	@Override
	public String getEmail() {
		return isAuthenticated() ? getUser().getEmail() : null;
	}

	@Override
	public String getUsername() {
		return isAuthenticated() ? getUser().getUsername() : null;
	}

	@Override
	public boolean hasRole( String authority ) {
		return isAuthenticated() && getUser().hasRole( authority );
	}

	@Override
	public boolean hasRole( Role role ) {
		return isAuthenticated() && getUser().hasRole( role );
	}

	@Override
	public boolean hasPermission( String name ) {
		return isAuthenticated() && getUser().hasPermission( name );
	}

	@Override
	public boolean hasPermission( Permission permission ) {
		return isAuthenticated() && getUser().hasPermission( permission );
	}

	@Override
	public boolean hasAuthority( String name ) {
		return isAuthenticated() && hasPermission( name );
	}

	@Override
	public User getUser() {
		return isAuthenticated() ? (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
	}

	@Override
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated() && authentication
				.getPrincipal() instanceof User;
	}
}
