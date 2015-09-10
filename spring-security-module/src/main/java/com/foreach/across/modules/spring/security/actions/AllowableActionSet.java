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

import java.util.Collection;
import java.util.HashSet;

/**
 * Fixed set of {@link com.foreach.across.modules.spring.security.actions.AllowableAction}s.
 * This implementation can also be used to convert a transient
 * {@link com.foreach.across.modules.spring.security.actions.AllowableActions} implementation - for example one
 * that evaluates every call against the security principal at that moment - to a fixed set.
 *
 * @author Arne Vandamme
 */
public class AllowableActionSet extends HashSet<AllowableAction> implements AllowableActions
{
	public AllowableActionSet() {
	}

	public AllowableActionSet( Collection<? extends AllowableAction> c ) {
		super( c );
	}

	public AllowableActionSet( Iterable<? extends AllowableAction> actions ) {
		for ( AllowableAction action : actions ) {
			add( action );
		}
	}

	public AllowableActionSet( String... actionIds ) {
		this( AllowableAction.toAllowableActions( actionIds ) );
	}

	@Override
	public boolean contains( AllowableAction action ) {
		return super.contains( action );
	}
}
