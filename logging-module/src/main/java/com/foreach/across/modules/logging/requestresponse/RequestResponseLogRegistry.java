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
package com.foreach.across.modules.logging.requestresponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class RequestResponseLogRegistry
{
	private int maxEntries = 100;
	private final LinkedList<RequestResponseLogEntry> entries = new LinkedList<>();

	public void setMaxEntries( int maxEntries ) {
		this.maxEntries = maxEntries;
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	public synchronized void add( RequestResponseLogEntry entry ) {
		entries.addFirst( entry );

		while ( entries.size() > maxEntries ) {
			entries.removeLast();
		}
	}

	public int size() {
		return entries.size();
	}

	public Collection<RequestResponseLogEntry> getEntries() {
		return new ArrayList<>( entries );
	}

	public RequestResponseLogEntry getEntry( UUID id ) {
		for ( RequestResponseLogEntry entry : getEntries() ) {
			if ( entry.getId().equals( id ) ) {
				return entry;
			}
		}

		return null;
	}
}
