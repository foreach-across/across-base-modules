package com.foreach.across.modules.entity.business;

import com.foreach.across.modules.entity.config.EntityConfiguration;

import java.io.Serializable;

/**
 * Wraps around an entity instance providing access to the generated properties.
 */
public class EntityWrapper
{
	private final EntityConfiguration entityConfiguration;

	private Object entity;

	public EntityWrapper( EntityConfiguration entityConfiguration, Object entity ) {
		this.entityConfiguration = entityConfiguration;
		this.entity = entity;
	}

	@SuppressWarnings("unchecked")
	public Serializable getId() {
		return entityConfiguration.getEntityModel().getId( entity );
	}

	public String getEntityLabel() {
		return entity != null ? entity.toString() : "unknown";
	}

	/**
	 * @return The entity instance that is being wrapped.
	 */
	public Object getEntity() {
		return entity;
	}
}
