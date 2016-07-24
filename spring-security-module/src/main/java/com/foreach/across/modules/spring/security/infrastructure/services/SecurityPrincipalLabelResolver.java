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

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;

import java.util.Optional;

/**
 * Implementations are responsible for resolving a unique principal name into a more appropriate UI label
 * (for example the full name of a user).
 *
 * @author Arne Vandamme
 * @see SecurityPrincipalLabelResolverStrategy
 * @since 2.0.0
 */
public interface SecurityPrincipalLabelResolver
{
	/**
	 * Build the label for a security principal.
	 *
	 * @param principal to resolve
	 * @return label or empty value if unable to resolvePrincipalLabel this principal
	 */
	Optional<String> resolvePrincipalLabel( SecurityPrincipal principal );
}
