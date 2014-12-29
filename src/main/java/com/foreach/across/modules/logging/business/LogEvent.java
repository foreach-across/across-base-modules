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
}
