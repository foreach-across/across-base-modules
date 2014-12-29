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
