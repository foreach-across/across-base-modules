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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.support.WritableAttributes;

/**
 * @author Arne Vandamme
 */
public interface ViewCreationContext extends WritableAttributes
{
	/**
 	 * @return EntityConfiguration the view is generated for.
	 */
	EntityConfiguration getEntityConfiguration();

	void setEntityConfiguration( EntityConfiguration entityConfiguration );

	EntityAssociation getEntityAssociation();

	void setEntityAssociation( EntityAssociation entityAssociation );

	boolean isForAssociation();
}
