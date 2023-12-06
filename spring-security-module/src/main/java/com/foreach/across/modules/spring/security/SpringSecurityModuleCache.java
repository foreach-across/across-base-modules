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
package com.foreach.across.modules.spring.security;

/**
 * @author Arne Vandamme
 */
public interface SpringSecurityModuleCache
{
	/**
	 * Unless property for {@link org.springframework.cache.annotation.Cacheable} that will ensure only null
	 * values are cached and otherwise assumes that caching has been performed by a repository.
	 */
	String UNLESS_NULLS_ONLY =
			"true && (#result instanceof T(java.util.Optional) && #result.isPresent() ) || (not (#result instanceof T(java.util.Optional)) && #result != null)";

	String SECURITY_PRINCIPAL = "securityPrincipalCache";
}
