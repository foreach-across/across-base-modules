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
package com.foreach.across.modules.hibernate.dto;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * Base DTO that provides a long id that can be used to insert ids manually.
 *
 * @author Arne Vandamme
 */
@Deprecated
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
