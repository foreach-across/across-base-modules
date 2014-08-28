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
