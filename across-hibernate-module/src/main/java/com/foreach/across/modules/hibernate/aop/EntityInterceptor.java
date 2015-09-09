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

public interface EntityInterceptor<T>
{
	/**
	 * Boolean method that checks if the interceptor should be executed for a particular
	 * target entity class.
	 *
	 * @param entityClass the interceptor should be checked for
	 * @return true if the interceptor should be applied
	 */
	boolean handles( Class<?> entityClass );

	void beforeCreate( T entity );

	void afterCreate( T entity );

	void beforeUpdate( T entity );

	void afterUpdate( T entity );

	void beforeDelete( T entity );

	void afterDelete( T entity );

	void beforeDeleteAll( Class<?> entityClass );

	void afterDeleteAll( Class<?> entityClass );
}
