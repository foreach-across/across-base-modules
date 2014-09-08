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
package com.foreach.across.modules.spring.security.acl.aop;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Defines the pointcut for intercepting BasicRepository persistence methods.
 *
 * @author Arne Vandamme
 */
public class BasicRepositoryAclPointcut extends StaticMethodMatcherPointcut
{
	@Override
	public boolean matches( Method method, Class<?> targetClass ) {
		Class<?> userClass = ClassUtils.getUserClass( targetClass );

		return BasicRepository.class.isAssignableFrom( userClass ) && isEntityMethod( method );
	}

	static boolean isEntityMethod( Method method ) {
		switch ( method.getName() ) {
			case BasicRepositoryAclInterceptor.CREATE:
			case BasicRepositoryAclInterceptor.UPDATE:
			case BasicRepositoryAclInterceptor.DELETE:
				break;
			default:
				return false;
		}

		return method.getParameterTypes().length == 1;
	}
}
