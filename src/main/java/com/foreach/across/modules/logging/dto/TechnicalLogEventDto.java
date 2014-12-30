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

import com.foreach.across.modules.logging.business.LogLevel;
import com.foreach.across.modules.logging.business.LogType;

public class TechnicalLogEventDto extends LogEventDto
{
	private String message;
	private LogLevel level;
	private Class sender;

	public TechnicalLogEventDto() {
		super();
		setLogType( LogType.TECHNICAL );
	}

	public String getMessage() {
		return message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel( LogLevel level ) {
		this.level = level;
	}

	public Class getSender() {
		return sender;
	}

	public void setSender( Class sender ) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return String.format( "%s. Extra data: %s", this.getMessage(), this.getData() );
	}
}
