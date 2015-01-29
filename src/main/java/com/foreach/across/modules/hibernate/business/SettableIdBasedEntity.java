package com.foreach.across.modules.hibernate.business;

import org.springframework.data.domain.Persistable;

import javax.persistence.Transient;
import java.util.Objects;

/**
 * Base class for an entity with a unique long id, that allows the id to be manually set.
 * This requires the use of a generator strategy that supports manual ids.
 *
 * @see com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator
 */
public abstract class SettableIdBasedEntity<T extends Persistable<Long>>
		implements IdBasedEntity, Persistable<Long>, EntityWithDto<T>
{
	@Transient
	private Long newEntityId;

	public abstract void setId( Long id );

	/**
	 * Returns if the {@code Persistable} is new or was persisted already.
	 *
	 * @return if the object is new
	 */
	@Override
	public final boolean isNew() {
		return getId() == null || getId() == 0;
	}

	/**
	 * Forcibly set the entity as new with the specific id.  When saving this instance, a new entity
	 * should be persisted with the given id.
	 *
	 * @param newEntityId id this new entity should be assigned to
	 */
	public void setNewEntityId( Long newEntityId ) {
		if ( newEntityId != null ) {
			setId( null );
			this.newEntityId = newEntityId;
		}
	}

	/**
	 * Returns the id that will forcibly be assigned when persisting this entity.
	 * This method will only return a non-null call if the entity is new and not yet persisted.
	 *
	 * @return new entity id
	 * @see #getId()
	 */
	public Long getNewEntityId() {
		return getId() == null ? newEntityId : null;
	}

	/**
	 * Two non-new entities are considered equal if they have the same id.
	 * New entities are only considered equal if they are in fact the same instance.
	 */
	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		SettableIdBasedEntity that = (SettableIdBasedEntity) o;

		if ( isNew() && that.isNew() ) {
			return this == that;
		}

		return getId().equals( that.getId() );
	}

	@Override
	public int hashCode() {
		return isNew() ? super.hashCode() : Objects.hash( getId() );
	}
}
