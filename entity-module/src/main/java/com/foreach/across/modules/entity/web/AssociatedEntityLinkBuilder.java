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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Arne Vandamme
 */
public class AssociatedEntityLinkBuilder
{
	private final EntityLinkBuilder fromLinkBuilder;
	private final EntityLinkBuilder toLinkBuilder;

	private String overviewPath = "{0}/associations/{1}";
	private String createPath = "{0}/associations/{1}/create";
	private String viewPath = "{0}/associations/{1}/{2,number,#}";
	private String updatePath = "{0}/associations/{1}/{2,number,#}/update";
	private String deletePath = "{0}/associations/{1}/{2,number,#}/delete";

	public AssociatedEntityLinkBuilder(
			EntityLinkBuilder fromLinkBuilder,
			EntityLinkBuilder toLinkBuilder
	) {
		this.fromLinkBuilder = fromLinkBuilder;
		this.toLinkBuilder = toLinkBuilder;
	}

	public String overview( Object parent ) {
		return fromLinkBuilder.view( parent ) + StringUtils.replaceOnce( toLinkBuilder.overview(), "entities",
		                                                                 "associations" );
	}
/*
	public String create() {
		return format( createPath );
	}

	public String update( Object entity ) {
		return format( updatePath, entity );
	}

	public String delete( Object entity ) {
		return format( deletePath, entity );
	}

	public String view( Object entity ) {
		return format( viewPath, entity );
	}

	private String format( String pattern ) {
		return MessageFormat.format( pattern, rootPath, entityConfiguration.getName(), null );
	}

	@SuppressWarnings("unchecked")
	private String format( String pattern, Object entity ) {
		Serializable id = entityConfiguration.getEntityModel().getId( entity );
		return MessageFormat.format( pattern, rootPath, entityConfiguration.getName(), id );
	}
	*/
}
