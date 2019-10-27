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

import java.util.Objects;

/**
 * Base implementation of {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}
 * ensuring the contract where toString() returns the principal name.
 *
 * @author Arne Vandamme
 */
public abstract class AbstractSecurityPrincipal implements SecurityPrincipal
{
	@Override
	public final String toString() {
		return Objects.toString( getSecurityPrincipalId(), null );
	}
}
