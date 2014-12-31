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

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lm_log_event")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
		name = "log_type_id",
		discriminatorType = DiscriminatorType.INTEGER
)
public abstract class LogEvent implements IdBasedEntity
{
	@Id
	@GeneratedValue(generator = "seq_lm_log_event_id")
	@GenericGenerator(
			name = "seq_lm_log_event_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName",
					                                     value = "seq_lm_log_event_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "10")
			}
	)
	private long id;

	@Column(name = "time", nullable = false)
	private Date time;

	@Column(name = "data")
	private String data;

	@Override
	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
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

	public abstract LogType getLogType();

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		LogEvent logEvent = (LogEvent) o;

		if ( id != logEvent.id ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return (int) ( id ^ ( id >>> 32 ) );
	}
}
