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

/**
 * <p>Base class for an interceptor hooked to repository persistence events.  Which repositories are
 * supported is module/implementation dependant.</p>
 * <p>Currently implementations are available for {@link com.foreach.across.modules.hibernate.repositories.BasicRepository}
 * and {@link org.springframework.data.jpa.repository.JpaRepository}.</p>
 *
 * @author Arne Vandamme
 */
public abstract class EntityInterceptorAdapter<T> implements EntityInterceptor<T>
{
	@Override
	public void beforeCreate( T entity ) {
	}

	@Override
	public void beforeUpdate( T entity ) {
	}

	@Override
	public void afterDelete( T entity ) {
	}

	@Override
	public void afterCreate( T entity ) {
	}

	@Override
	public void afterUpdate( T entity ) {
	}

	@Override
	public void beforeDelete( T entity ) {
	}

	@Override
	public void beforeDeleteAll( Class<T> entityClass ) {
	}

	@Override
	public void afterDeleteAll( Class<T> entityClass ) {
	}
}
