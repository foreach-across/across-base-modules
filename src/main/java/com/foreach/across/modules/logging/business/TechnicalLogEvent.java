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
package com.foreach.across.modules.logging.business;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue(LogType.Constants.TECHNICAL)
@Table(name = "lm_technical_log_event")
public class TechnicalLogEvent extends LogEvent
{
	@Column(name = "message", nullable = false)
	private String message;

	@Column(name = "level", nullable = false)
	@Type(type = HibernateLogLevel.CLASS_NAME)
	private LogLevel level;

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

	@Override
	public LogType getLogType() {
		return LogType.TECHNICAL;
	}
}
