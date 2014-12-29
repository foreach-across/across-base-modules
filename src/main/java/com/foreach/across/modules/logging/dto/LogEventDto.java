package com.foreach.across.modules.logging.dto;

import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.logging.business.LogEvent;
import com.foreach.across.modules.logging.business.LogType;

import java.util.Date;

public class LogEventDto extends IdBasedEntityDto<LogEvent>
{
	private Date time = new Date();
	private String data;
	private LogType logType;

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
