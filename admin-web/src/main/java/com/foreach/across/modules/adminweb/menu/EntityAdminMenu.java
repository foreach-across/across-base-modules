package com.foreach.across.modules.adminweb.menu;

import com.foreach.across.modules.web.menu.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class EntityAdminMenu<T> extends Menu
{
	private final Class<T> entityClass;
	private final T entity;

	public EntityAdminMenu( Class<T> entityClass ) {
		this( entityClass, null );
	}

	public EntityAdminMenu( Class<T> entityClass, T entity ) {
		super( StringUtils.uncapitalize( entityClass.getSimpleName() ) + "EntityAdminMenu" );
		Assert.notNull( entityClass );

		this.entityClass = entityClass;
		this.entity = entity;
	}

	public Class<T> getEntityType() {
		return entityClass;
	}

	public T getEntity() {
		return entity;
	}
}
