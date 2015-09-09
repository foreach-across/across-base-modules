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
package com.foreach.across.modules.hibernate.aop;

import java.lang.reflect.ParameterizedType;

/**
 * Base class for an interceptor hooked to {@link com.foreach.across.modules.hibernate.repositories.BasicRepository}
 * persistence methods.
 * <p/>
 * Implementations will be picked up automatically by the
 * {@link com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptor} if it is active.
 *
 * @author Arne Vandamme
 */
public abstract class EntityInterceptorAdapter<T> implements EntityInterceptor<T>
{
	protected final Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public EntityInterceptorAdapter() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	@Override
	public Class<T> getEntityClass() {
		return entityClass;
	}

	@Override
	public void beforeCreate( T entity ) {
	}

	@Override
	public void beforeUpdate( T Entity ) {
	}

	@Override
	public void afterDelete( T entity, boolean isSoftDelete ) {
	}

	@Override
	public void afterCreate( T entity ) {
	}

	@Override
	public void afterUpdate( T entity ) {
	}

	@Override
	public void beforeDelete( T entity, boolean isSoftDelete ) {
	}
}
