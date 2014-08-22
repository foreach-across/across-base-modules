package com.foreach.across.modules.hibernate.dto;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * Base DTO that provides a long id that can be used to insert ids manually.
 *
 * @author Arne Vandamme
 */
public class IdBasedEntityDto<T extends IdBasedEntity>
{
    private long id;
    private Boolean newEntity;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public boolean isNewEntity() {
        return newEntity != null ? newEntity : getId() == 0;
    }

    public void setNewEntity( boolean newEntity ) {
        this.newEntity = newEntity;
    }

	public void copyFrom( T source ) {
		BeanUtils.copyProperties( source, this );
	}

    @SuppressWarnings( "all" )
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof IdBasedEntityDto ) ) {
            return false;
        }

        IdBasedEntityDto that = (IdBasedEntityDto) o;

        return Objects.equals( id, that.id );
    }

    @SuppressWarnings( "all" )
    @Override
    public int hashCode() {
        return Objects.hash( id );
    }
}
