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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class BasicRepositoryImpl<T> implements BasicRepository<T>
{
	private final Class<T> clazz;
	private static final int MAX_RESULTS = 5000;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public BasicRepositoryImpl() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.clazz = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	@Override
	public Class<T> getEntityClass() {
		return clazz;
	}

	/**
	 * Creates a query for the distinct root entity.
	 */
	protected Criteria distinct() {
		return session()
				.createCriteria( clazz )
				.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY );
	}

	/**
	 * Creates a query for the distinct root entity with the default order applied.
	 */
	protected Criteria orderedDistinct() {
		return ordered( distinct() );
	}

	protected Session session() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Adds the default order to the criteria.
	 */
	protected Criteria ordered( Criteria criteria ) {
		return criteria;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	@Override
	public T getById( long id ) {
		return (T) session().get( clazz, id );
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<T> getAll() {
		// TODO: https://bitbucket.org/beforeach/across-standard-modules/issue/15/
		return (Collection<T>) orderedDistinct().setFirstResult( 0 ).setMaxResults( MAX_RESULTS ).list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<T> getAllForIds( Collection<Long> ids ) {
		// TODO: https://bitbucket.org/beforeach/across-standard-modules/issue/15/
		return (Collection<T>) orderedDistinct()
				.add( Restrictions.in( "id", ids ) )
				.setFirstResult( 0 ).setMaxResults( MAX_RESULTS ).list();
	}

	@Transactional
	@Override
	public void create( T object ) {
		session().save( object );
	}

	@Transactional
	@Override
	public void update( T object ) {
		Session session = session();

		if ( !session.contains( object ) ) {
			session.merge( object );
		}
		else {
			session.update( object );
		}
	}

	@Transactional
	@Override
	public void delete( T object ) {
		if ( object instanceof Undeletable ) {
			( (Undeletable) object ).setDeleted( true );
			session().saveOrUpdate( object );
		}
		else {
			session().delete( object );
		}
	}
}