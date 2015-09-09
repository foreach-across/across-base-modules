package com.foreach.across.modules.hibernate.business;

import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class SettableIdAuditableEntity<T extends Persistable<Long>> extends SettableIdBasedEntity<T>
		implements Auditable<String>
{
	@Column(name = "created_by", nullable = true)
	private String createdBy;

	@Column(name = "created_date", nullable = true)
	private Date createdDate;

	@Column(name = "last_modified_by", nullable = true)
	private String lastModifiedBy;

	@Column(name = "last_modified_date", nullable = true)
	private Date lastModifiedDate;

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy( String createdBy ) {
		this.createdBy = createdBy;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate( Date createdDate ) {
		this.createdDate = createdDate;
	}

	@Override
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy( String lastModifiedBy ) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate( Date lastModifiedDate ) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
