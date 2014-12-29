package com.foreach.across.modules.logging.dto;

public class FunctionalLogEventDto extends LogEventDto
{
	private String action;
	private String entity;
	private long entityId;
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
	public String toString() {
		return String.format( "User %s took action %s on %s %d. Extra data: %s", this.getUser(), this.getAction(),
		                      this.getEntity(), this.getEntityId(), this.getData() );
	}
}
