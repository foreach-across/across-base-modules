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

import java.util.Collection;

/**
 * Represents an entity that has one or more {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}
 * instances as parent.  Example would be user that belongs to one or more groups.  The entity does not have to be a
 * security principal in itself, but in permission checking situations, it will be considered to have all the
 * authorities of the parents it belongs to.
 *
 * @author Arne Vandamme
 */
public interface SecurityPrincipalHierarchy
{
	Collection<SecurityPrincipal> getParentPrincipals();
}
