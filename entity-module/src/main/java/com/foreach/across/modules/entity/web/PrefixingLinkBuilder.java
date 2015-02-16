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

import org.springframework.util.Assert;

/**
 * Prefxes paths generated from another {@link com.foreach.across.modules.entity.web.EntityLinkBuilder} instance.
 *
 * @author Arne Vandamme
 */
public class PrefixingLinkBuilder implements EntityLinkBuilder
{
	private final String prefixPath;
	private final EntityLinkBuilder linkBuilder;

	public PrefixingLinkBuilder( String prefixPath, EntityLinkBuilder linkBuilder ) {
		Assert.notNull( prefixPath );
		this.prefixPath = prefixPath;
		this.linkBuilder = linkBuilder;
	}

	@Override
	public String overview() {
		return prefixPath + linkBuilder.overview();
	}

	@Override
	public String create() {
		return prefixPath + linkBuilder.create();
	}

	@Override
	public String update( Object entity ) {
		return prefixPath + linkBuilder.update( entity );
	}

	@Override
	public String delete( Object entity ) {
		return prefixPath + linkBuilder.delete( entity );
	}

	@Override
	public String view( Object entity ) {
		return prefixPath + linkBuilder.view( entity );
	}

	@Override
	public String associations( Object entity ) {
		return prefixPath + linkBuilder.associations( entity );
	}

	@Override
	public EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity ) {
		return new PrefixingLinkBuilder( sourceLinkBuilder.associations( sourceEntity ), this );
	}
}
