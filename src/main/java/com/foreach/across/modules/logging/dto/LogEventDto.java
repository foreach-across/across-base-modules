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

import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.logging.business.LogEvent;
import com.foreach.across.modules.logging.business.LogType;

import java.util.Date;

public class LogEventDto extends IdBasedEntityDto<LogEvent>
{
	private Date time;
	private String data;
	private LogType logType;

	public LogEventDto() {
		setTime( new Date() );
	}

	public Date getTime() {
		return time;
	}

	public void setTime( Date time ) {
		this.time = time;
	}

	public String getData() {
		return data;
	}

	public void setData( String data ) {
		this.data = data;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType( LogType logType ) {
		this.logType = logType;
	}
}
