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
package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;

/**
 * Generates links for an {@link com.foreach.across.modules.entity.registry.EntityAssociation}.
 * This link builder is the base for scoped versions created with {@link #asAssociationFor(EntityLinkBuilder, Object)},
 * direct use is rare.
 *
 * @author Arne Vandamme
 */
public class EntityAssociationLinkBuilder extends EntityConfigurationLinkBuilder
{
	private final EntityAssociation association;

	public EntityAssociationLinkBuilder( EntityAssociation association, ConversionService conversionService ) {
		super( StringUtils.EMPTY, association.getTargetEntityConfiguration(), conversionService );

		this.association = association;
	}

	@Override
	protected String getEntityConfigurationPath() {
		return association.getName();
	}
}
