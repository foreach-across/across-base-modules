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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import org.springframework.core.convert.ConversionService;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Creates links to entity views and standard controllers.
 * By default crud and list views are included.
 */
public class EntityConfigurationLinkBuilder implements EntityLinkBuilder
{
	private final String rootPath;
	private final EntityConfiguration entityConfiguration;
	private final ConversionService conversionService;

	private String overviewPath = "{0}/{1}";
	private String createPath = "{0}/{1}/create";
	private String viewPath = "{0}/{1}/{2}";
	private String updatePath = "{0}/{1}/{2}/update";
	private String deletePath = "{0}/{1}/{2}/delete";
	private String associationsPath = "{0}/{1}/{2}/associations";

	public EntityConfigurationLinkBuilder( String rootPath,
	                                       EntityConfiguration entityConfiguration,
	                                       ConversionService conversionService ) {
		this.rootPath = rootPath;
		this.entityConfiguration = entityConfiguration;
		this.conversionService = conversionService;
	}

	protected String getOverviewPath() {
		return overviewPath;
	}

	protected String getCreatePath() {
		return createPath;
	}

	protected String getViewPath() {
		return viewPath;
	}

	protected String getUpdatePath() {
		return updatePath;
	}

	protected String getDeletePath() {
		return deletePath;
	}

	protected String getAssociationsPath() {
		return associationsPath;
	}

	public void setOverviewPath( String overviewPath ) {
		this.overviewPath = overviewPath;
	}

	public void setCreatePath( String createPath ) {
		this.createPath = createPath;
	}

	public void setViewPath( String viewPath ) {
		this.viewPath = viewPath;
	}

	public void setUpdatePath( String updatePath ) {
		this.updatePath = updatePath;
	}

	public void setDeletePath( String deletePath ) {
		this.deletePath = deletePath;
	}

	public void setAssociationsPath( String associationsPath ) {
		this.associationsPath = associationsPath;
	}

	@Override
	public String overview() {
		return format( overviewPath );
	}

	@Override
	public String create() {
		return format( createPath );
	}

	@Override
	public String update( Object entity ) {
		return format( updatePath, entity );
	}

	@Override
	public String delete( Object entity ) {
		return format( deletePath, entity );
	}

	@Override
	public String view( Object entity ) {
		return format( viewPath, entity );
	}

	@Override
	public String associations( Object entity ) {
		return format( associationsPath, entity );
	}

	private String format( String pattern ) {
		return MessageFormat.format( pattern, rootPath, getEntityConfigurationPath(), null );
	}

	@SuppressWarnings("unchecked")
	private String format( String pattern, Object entity ) {
		Serializable id = entityConfiguration.getEntityModel().getId( entity );
		String idAsString = conversionService.convert( id, String.class );

		return MessageFormat.format( pattern, rootPath, getEntityConfigurationPath(), idAsString );
	}

	protected String getEntityConfigurationPath() {
		return entityConfiguration.getName();
	}

	/**
	 * Creates a new link builder that represents the current linkbuilder as an association from a source entity,
	 * this will use {@link com.foreach.across.modules.entity.web.EntityLinkBuilder#associations(Object)} on the
	 * source link builder for prefixing the current link builder.
	 */
	@Override
	public EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity ) {
		return new PrefixingLinkBuilder( sourceLinkBuilder.associations( sourceEntity ), this );
	}
}
