package com.foreach.across.modules.hibernate.jpa.intercept;

import com.foreach.across.modules.hibernate.aop.EntityInterceptor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.ClassUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings( "unchecked" )
public class EntityInterceptorEntityListener
{
	@SuppressWarnings("unused")
	private static Collection<EntityInterceptor> entityInterceptors = Collections.emptyList();

	@PrePersist
	public void onPreInsert( Object entity ) {
		for ( EntityInterceptor interceptor : filterApplicable( entity, entityInterceptors ) ) {
			interceptor.beforeCreate( entity );
		}
	}

	@PostPersist
	public void onPostInsert( Object entity ) {
		for ( EntityInterceptor interceptor : filterApplicable( entity, entityInterceptors ) ) {
			interceptor.afterCreate( entity );
		}
	}

	@PreRemove
	public void onPreDelete( Object entity ) {
		for ( EntityInterceptor interceptor : filterApplicable( entity, entityInterceptors ) ) {
			interceptor.beforeDelete( entity, false );
		}
	}

	@PostRemove
	public void onPostDelete( Object entity ) {
		for ( EntityInterceptor interceptor : filterApplicable( entity, entityInterceptors ) ) {
			interceptor.afterDelete( entity, false );
		}
	}

	@PreUpdate
	public void onPreUpdate( Object entity ) {
		for ( EntityInterceptor interceptor : filterApplicable( entity, entityInterceptors ) ) {
			interceptor.beforeUpdate( entity );
		}
	}

	@PostUpdate
	public void onPostUpdate(Object entity ) {
		for ( EntityInterceptor interceptor : filterApplicable( entity, entityInterceptors ) ) {
			interceptor.afterUpdate( entity );
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<EntityInterceptor> filterApplicable( Object entity,
	                                                        Collection<EntityInterceptor> interceptors ) {
		Class<?> entityClass = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( entity ) );

		Collection<EntityInterceptor> matchingInterceptors = new ArrayList<>();

		for ( EntityInterceptor candidate : interceptors ) {
			if ( candidate.getEntityClass().equals( entityClass ) ) {
				matchingInterceptors.add( candidate );
			}
			else if ( candidate.getEntityClass().isAssignableFrom( entityClass ) ) {
				matchingInterceptors.add( candidate );
			}
		}

		return matchingInterceptors;
	}

	public static void setEntityInterceptors( Collection<EntityInterceptor> entityInterceptors ) {
		EntityInterceptorEntityListener.entityInterceptors = entityInterceptors;
	}
}
