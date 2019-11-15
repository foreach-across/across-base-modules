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

package com.foreach.across.modules.spring.security.infrastructure.business;

/**
 * To be implemented by any principal which represents an actual {@link SecurityPrincipal}.
 * Any authentication principal which implements this interface will cause
 * {@link com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy}
 * to load the actual target {@link SecurityPrincipal} using the id.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 * @see SecurityPrincipal
 * @see SecurityPrincipalId
 */
public interface SecurityPrincipalReference
{
	/**
	 * @return id of the principal that this instance represents
	 */
	SecurityPrincipalId getSecurityPrincipalId();
}
