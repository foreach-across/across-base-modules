package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import org.apache.commons.lang3.ClassUtils;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Creates links to entity views and standard controllers.
 * By default crud and list views are included.
 */
public class EntityLinkBuilder
{
	private final String rootPath;
	private final EntityConfiguration entityConfiguration;

	private String overviewPath = "{0}/{1}";
	private String createPath = "{0}/{1}/create";
	private String viewPath = "{0}/{1}/{2,number,#}";
	private String updatePath = "{0}/{1}/{2,number,#}/update";
	private String deletePath = "{0}/{1}/{2,number,#}/delete";

	public EntityLinkBuilder( String rootPath, EntityConfiguration entityConfiguration ) {
		this.rootPath = rootPath;
		this.entityConfiguration = entityConfiguration;

		if ( !isNumberIdType( entityConfiguration ) ) {
			viewPath = "{0}/{1}/{2}";
			updatePath = "{0}/{1}/{2}/update";
			deletePath = "{0}/{1}/{2}/delete";
		}
	}

	private boolean isNumberIdType( EntityConfiguration entityConfiguration ) {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		if ( entityModel != null ) {
			return ClassUtils.isAssignable( entityModel.getIdType(), Number.class );
		}

		return false;
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

	public String overview() {
		return format( overviewPath );
	}

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
}
