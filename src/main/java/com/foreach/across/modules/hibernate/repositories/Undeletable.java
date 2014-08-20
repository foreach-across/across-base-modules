package com.foreach.across.modules.hibernate.repositories;

public interface Undeletable
{
	boolean isDeleted();

	void setDeleted( boolean deleted );
}
