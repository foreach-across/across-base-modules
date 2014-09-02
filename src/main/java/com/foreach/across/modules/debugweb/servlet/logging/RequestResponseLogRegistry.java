package com.foreach.across.modules.debugweb.servlet.logging;

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
