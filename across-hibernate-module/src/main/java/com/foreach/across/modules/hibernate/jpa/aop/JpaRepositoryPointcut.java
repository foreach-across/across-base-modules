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
package com.foreach.across.modules.hibernate.jpa.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Pointcut that only wires entity interception for {@link JpaRepository} implementations
 * that have a {@link Persistable} entity type.
 *
 * @author Andy Somers
 */
public class JpaRepositoryPointcut extends StaticMethodMatcherPointcut
{
	private static final Logger LOG = LoggerFactory.getLogger( JpaRepositoryPointcut.class );
	private static final ClassFilter CLASS_FILTER = clazz -> JpaRepository.class.isAssignableFrom( ClassUtils.getUserClass( clazz ) );

	@Override
	public ClassFilter getClassFilter() {
		return CLASS_FILTER;
	}

	@Override
	public boolean matches( Method method, Class<?> targetClass ) {
		Class entityClass = TypeDescriptor.valueOf( targetClass )
		                                  .upcast( JpaRepository.class )
		                                  .getResolvableType()
		                                  .getGeneric( 0 )
		                                  .resolve();

		if ( !Persistable.class.isAssignableFrom( entityClass ) ) {
			LOG.warn(
					"JPA repository {} detected without Persistable type parameter - entity interception is not possible.",
					targetClass );
			return false;
		}
		else {
			return isEntityMethod( method );
		}
	}

	static boolean isEntityMethod( Method method ) {
		switch ( method.getName() ) {
			case JpaRepositoryInterceptor.SAVE:
			case JpaRepositoryInterceptor.SAVE_ALL:
			case JpaRepositoryInterceptor.SAVE_AND_FLUSH:
			case JpaRepositoryInterceptor.DELETE:
			case JpaRepositoryInterceptor.DELETE_IN_BATCH:
				return ( method.getParameterTypes().length == 1 );
			case JpaRepositoryInterceptor.DELETE_ALL:
				return method.getParameterTypes().length <= 1;
			case JpaRepositoryInterceptor.DELETE_ALL_IN_BATCH:
				return method.getParameterTypes().length == 0;
			default:
				return false;
		}
	}

}
