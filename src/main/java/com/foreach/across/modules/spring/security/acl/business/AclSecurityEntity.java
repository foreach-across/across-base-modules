package com.foreach.across.modules.spring.security.acl.business;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Represents a generic named entity that can be used for ACL control.
 * This is usually a more abstract concept: like the default system ACL that does not have any
 * other entity representation (unlike User records for example).
 *
 * An AclSecurityEntity can optionally have a single parent AclSecurityEntity.
 *
 * @author Arne Vandamme
 */
@Entity
@Table(name = "acl_entity")
public class AclSecurityEntity implements IdBasedEntity
{
	@Id
	@GeneratedValue(generator = "seq_acl_entity_id")
	@GenericGenerator(
			name = "seq_acl_entity_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_acl_entity_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@ManyToOne(optional = true)
	@JoinColumn(name = "parent_id")
	private AclSecurityEntity parent;

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public AclSecurityEntity getParent() {
		return parent;
	}

	public void setParent( AclSecurityEntity parent ) {
		this.parent = parent;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		AclSecurityEntity that = (AclSecurityEntity) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return (int) ( id ^ ( id >>> 32 ) );
	}
}
