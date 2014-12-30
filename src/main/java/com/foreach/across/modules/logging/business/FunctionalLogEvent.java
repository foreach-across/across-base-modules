package com.foreach.across.modules.logging.business;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue(LogType.Constants.FUNCTIONAL)
@Table(name = "lm_functional_log_event")
public class FunctionalLogEvent extends LogEvent
{
	@Column(name = "action", nullable = false)
	private String action;

	@Column(name = "entity", nullable = false)
	private String entity;

	@Column(name = "entityId", nullable = false)
	private long entityId;

	@Column(name = "log_user", nullable = false)
	private String user;

	public String getAction() {
		return action;
	}

	public void setAction( String action ) {
		this.action = action;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity( String entity ) {
		this.entity = entity;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId( long entityId ) {
		this.entityId = entityId;
	}

	public String getUser() {
		return user;
	}

	public void setUser( String user ) {
		this.user = user;
	}

	@Override
	public LogType getLogType() {
		return LogType.FUNCTIONAL;
	}
}
