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

/**
 * Represents an entire set of {@link com.foreach.across.modules.spring.security.actions.AllowableAction}s that
 * can be performed on an item.  An implementation is usually a contextual, possibly stateful object
 * depending on the current security principal and the item being worked on.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.spring.security.actions.AllowableActionSet
 */
public interface AllowableActions extends Iterable<AllowableAction>
{
	/**
	 * Checks if a certain action is allowed by verifying it is present in the collection.
	 *
	 * @param action that should be checked
	 * @return true if the action is allowed
	 */
	boolean contains( AllowableAction action );
}
