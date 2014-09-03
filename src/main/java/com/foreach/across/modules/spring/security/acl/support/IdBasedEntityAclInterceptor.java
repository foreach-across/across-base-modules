package com.foreach.across.modules.spring.security.acl.support;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;

/**
 * Base class for an interceptor hooked to {@link com.foreach.across.modules.hibernate.repositories.BasicRepository}
 * persistence methods.  Useful for creating ACLs when saving or deleting instances.
 * <p/>
 * Implementations will be picked up automatically by the
 * {@link com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptor} if it is active.
 *
 * @author Arne Vandamme
 */
public abstract class IdBasedEntityAclInterceptor<T extends IdBasedEntity>
{
	protected final Logger LOG = LoggerFactory.getLogger( ClassUtils.getUserClass( getClass() ) );

	private final Class<T> entityClass;

	@Autowired
	private AclSecurityService aclSecurityService;

	@SuppressWarnings("unchecked")
	public IdBasedEntityAclInterceptor() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	protected AclSecurityService aclSecurityService() {
		return aclSecurityService;
	}

	/**
	 * @return The current SecurityPrincipal or null in case there is no instance of SecurityPrincipal attached.
	 */
	protected SecurityPrincipal currentSecurityPrincipal() {
		if ( isAuthenticated() ) {
			Object principal = currentAuthentication().getPrincipal();

			if ( principal instanceof SecurityPrincipal ) {
				return (SecurityPrincipal) principal;
			}
		}

		return null;
	}

	protected boolean isAuthenticated() {
		return currentAuthentication().isAuthenticated();
	}

	protected Authentication currentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void beforeCreate( T entity ) {
	}

	public abstract void afterCreate( T entity );

	public void beforeUpdate( T Entity ) {
	}

	public abstract void afterUpdate( T entity );

	public abstract void beforeDelete( T entity, boolean isSoftDelete );

	public void afterDelete( T entity, boolean isSoftDelete ) {
	}
}
