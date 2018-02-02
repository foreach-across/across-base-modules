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
package com.foreach.across.modules.spring.security.actions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An abstraction of a single action that can be performed on an item.
 * Because an action is in essence a simple unique string, the system can easily be extended.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.spring.security.actions.AllowableActions
 */
@SuppressWarnings( "unused" )
public class AllowableAction implements Comparable<AllowableAction>
{
	/**
	 * Default actions mapped to common ACL permissions.
	 */
	public static final AllowableAction READ = new AllowableAction( "read" );
	public static final AllowableAction UPDATE = new AllowableAction( "update" );
	public static final AllowableAction CREATE = new AllowableAction( "create" );
	public static final AllowableAction DELETE = new AllowableAction( "delete" );
	public static final AllowableAction ADMINISTER = new AllowableAction( "administer" );

	private final String id;

	public AllowableAction( String id ) {
		Assert.isTrue( StringUtils.isNotBlank( id ) );

		this.id = id;
	}

	/**
	 * @return Unique id of the action.
	 */
	public String getId() {
		return id;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		AllowableAction that = (AllowableAction) o;

		if ( id != null ? !id.equals( that.id ) : that.id != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public int compareTo( AllowableAction other ) {
		return getId().compareTo( other.getId() );
	}

	/**
	 * Convert a {@code String} into an {@link AllowableAction} instance.
	 *
	 * @param id of the action
	 * @return instance
	 */
	public static AllowableAction from( String id ) {
		return new AllowableAction( id );
	}

	public static Collection<AllowableAction> toAllowableActions( String... ids ) {
		List<AllowableAction> actions = new ArrayList<>( ids.length );
		for ( String id : ids ) {
			actions.add( new AllowableAction( id ) );
		}
		return actions;
	}
}
