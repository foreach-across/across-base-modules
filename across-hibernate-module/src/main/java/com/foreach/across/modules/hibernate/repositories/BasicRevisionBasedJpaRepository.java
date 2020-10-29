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
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public abstract class BasicRevisionBasedJpaRepository<T extends RevisionBasedEntity<T>, U, R extends Revision<U>>
		extends RevisionBasedEntityManager<T, U, R>
{
	private final Class<T> clazz;
	private HibernateSessionHolder hibernateSessionHolder;

	@SuppressWarnings("unchecked")
	@Autowired
	public BasicRevisionBasedJpaRepository() {
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
		Collection<T> items = list( distinct( addOwnerRestriction( owner ) ) );

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
		return list( distinct( addOwnerRestriction( owner ), revisionSelector( Revision.LATEST ) ) );
	}

	/**
	 * @return All entities relevant in the specific revision.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Collection<T> getAllForSpecificRevision( U owner, int revisionNumber ) {
		return list( distinct( addOwnerRestriction( owner ), revisionSelector( revisionNumber ) ) );
	}

	/**
	 * @return All entities relevant in the draft revision.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Collection<T> getAllForDraftRevision( U owner ) {
		return list( distinct( addOwnerRestriction( owner ), revisionSelector( Revision.DRAFT ) ) );
	}

	protected Collection<T> list( CriteriaQuery<T> criteriaQuery ) {
		return session().createQuery( criteriaQuery ).list();
	}

	/**
	 * Add owner restriction to the partically configured criteria.
	 */
	protected abstract Specification<T> addOwnerRestriction( U owner );

	protected CriteriaQuery<T> distinct( Specification<T>... specifications ) {
		CriteriaBuilder cb = session().getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery( clazz );
		Root<T> from = query.from( clazz );
		Predicate[] predicates = Arrays.stream( specifications ).map( s -> s.toPredicate( from, query, cb ) ).toArray( Predicate[]::new );
		return query.distinct( true ).where( predicates );
	}

	protected Specification<T> revisionSelector( int revisionNumber ) {
		switch ( revisionNumber ) {
			case Revision.DRAFT:
				return ( root, query, cb ) -> cb.and(
						cb.or(
								cb.equal( root.get( "firstRevision" ), Revision.DRAFT ),
								cb.equal( root.get( "removalRevision" ), 0 )
						)
				);
			case Revision.LATEST:
				return ( root, query, cb ) -> cb.and(
						cb.ge( root.get( "firstRevision" ), 0 ),
						cb.equal( root.get( "removalRevision" ), 0 )
				);
			default:
				return ( root, query, cb ) -> cb.and(
						cb.ge( root.get( "firstRevision" ), 0 ),
						cb.le( root.get( "firstRevision" ), revisionNumber ),
						cb.or(
								cb.equal( root.get( "removalRevision" ), 0 ),
								cb.gt( root.get( "removalRevision" ), revisionNumber )
						)
				);
		}
	}

	protected Session session() {
		return hibernateSessionHolder.getCurrentSession();
	}

	@Autowired
	public void setHibernateSessionHolder( HibernateSessionHolder hibernateSessionHolder ) {
		this.hibernateSessionHolder = hibernateSessionHolder;
	}
}
