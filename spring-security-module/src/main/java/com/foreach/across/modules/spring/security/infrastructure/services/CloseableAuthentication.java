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
package com.foreach.across.modules.spring.security.infrastructure.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implementation that will set the current security context to the given
 * {@link org.springframework.security.core.Authentication} instance upon creation, and will reset to the previous
 * authentication when {@link #close()} is called.  The instance is expected to be disposed after closing.
 *
 * @author Arne Vandamme
 */
public class CloseableAuthentication implements AutoCloseable
{
	private Authentication previous;
	private boolean closed;

	public CloseableAuthentication( Authentication newAuthentication ) {
		previous = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication( newAuthentication );
	}

	@Override
	public void close() {
		if ( !closed ) {
			closed = true;
			SecurityContextHolder.getContext().setAuthentication( previous );
			previous = null;
		}
	}
}
