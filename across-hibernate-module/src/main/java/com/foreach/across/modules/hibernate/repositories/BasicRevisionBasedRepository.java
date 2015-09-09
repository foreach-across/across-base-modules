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
package com.foreach.across.modules.hibernate.repositories;

import com.foreach.across.core.revision.Revision;
import com.foreach.across.core.revision.RevisionBasedEntity;
import com.foreach.across.core.revision.RevisionBasedEntityManager;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public abstract class BasicRevisionBasedRepository<T extends RevisionBasedEntity<T>, U, R extends Revision<U>>
		extends RevisionBasedEntityManager<T, U, R>
{
	private final Class<T> clazz;

	@Autowired
	private HibernateSessionHolder hibernateSessionHolder;

	@SuppressWarnings("unchecked")
	public BasicRevisionBasedRepository() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.clazz = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	/**
	 * Inserts a new entity in the data store.
	 *
	 * @param entity New entity instance that should be stored.
	 */
	@Override
	protected void insert( T entity ) {
		session().save( entity );
	}

	/**
	 * Updates the entity value with the current first and last revision in the datastore.
	 * The new value, first and last revision are set on the entity instance.
	 *
	 * @param entity               New entity instance that should be stored.
	 * @param currentFirstRevision Currently stored first revision.
	 * @param currentLastRevision  Currently stored last revision.
	 */
	@Override
	protected void update( T entity, int currentFirstRevision, int currentLastRevision ) {
		session().update( entity );
	}

	/**
	 * Removes an entity from the data store.
	 *
	 * @param entity Entity instance that should be removed.
	 */
	@Override
	protected void delete( T entity ) {
		if ( entity instanceof Undeletable ) {
			( (Undeletable) entity ).setDeleted( true );
			session().saveOrUpdate( entity );
		}
		else {
			session().delete( entity );
		}
	}

	/**
	 * Delete all entities across all revisions for the owner.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void deleteAllForOwner( U owner ) {
		List<T> items = (List<T>) addOwnerRestriction( distinct(), owner ).list();

		for ( T item : items ) {
			delete( item );
		}
	}

	/**
	 * @return All entities relevant for the current/latest revision.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Collection<T> getAllForLatestRevision( U owner ) {
		return (Collection<T>) addOwnerRestriction( revisionSelector( Revision.LATEST ), owner )
				.list();
	}

	/**
	 * @return All entities relevant in the specific revision.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Collection<T> getAllForSpecificRevision( U owner, int revisionNumber ) {
		return (Collection<T>) addOwnerRestriction( revisionSelector( revisionNumber ), owner )
				.list();
	}

	/**
	 * @return All entities relevant in the draft revision.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Collection<T> getAllForDraftRevision( U owner ) {
		return (Collection<T>) addOwnerRestriction( revisionSelector( Revision.DRAFT ), owner )
				.list();
	}

	/**
	 * Add owner restriction to the partically configured criteria.
	 */
	protected abstract Criteria addOwnerRestriction( Criteria criteria, U owner );

	protected Criteria distinct() {
		return session()
				.createCriteria( clazz )
				.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY );
	}

	protected Criteria revisionSelector( int revisionNumber ) {
		Criteria cr = distinct();

		switch ( revisionNumber ) {
			case Revision.DRAFT:
				cr.add(
						Restrictions.or(
								Restrictions.eq( "firstRevision", Revision.DRAFT ),
								Restrictions.eq( "removalRevision", 0 )
						)
				);
				break;
			case Revision.LATEST:
				cr.add( Restrictions.ge( "firstRevision", 0 ) );
				cr.add( Restrictions.eq( "removalRevision", 0 ) );
				break;
			default:
				cr.add( Restrictions.ge( "firstRevision", 0 ) );
				cr.add( Restrictions.le( "firstRevision", revisionNumber ) );
				cr.add(
						Restrictions.or(
								Restrictions.eq( "removalRevision", 0 ),
								Restrictions.gt( "removalRevision", revisionNumber )
						)
				);
				break;
		}

		return cr;
	}

	protected Session session() {
		return hibernateSessionHolder.getCurrentSession();
	}
}
