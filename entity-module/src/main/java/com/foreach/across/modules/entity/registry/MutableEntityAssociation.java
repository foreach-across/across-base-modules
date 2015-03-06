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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.support.WritableAttributes;

/**
 * @author Arne Vandamme
 */
public interface MutableEntityAssociation extends EntityAssociation, WritableAttributes, ConfigurableEntityViewRegistry
{
	/**
	 * Should the association be hidden or explicitly shown in the administrative UI.  If the property is not set
	 * explicitly on the association (or set to null), an association should be hidden if any of its participating
	 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration} instances is hidden.
	 *
	 * @param hidden true if should not show in the UI, can be null to revert to default check
	 */
	void setHidden( Boolean hidden );

	void setTargetEntityConfiguration( EntityConfiguration entityConfiguration );

	void setSourceProperty( EntityPropertyDescriptor descriptor );

	void setTargetProperty( EntityPropertyDescriptor descriptor );
}
