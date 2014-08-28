package com.foreach.across.modules.spring.security.acl.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.registry.IncrementalRefreshableRegistry;
import com.foreach.across.core.registry.RefreshableRegistry;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptor;
import com.foreach.across.modules.spring.security.acl.support.IdBasedEntityAclInterceptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * Configures intercepting the BasicRepository methods to create or delete ACLs when an entity
 * gets inserted/updated/deleted.
 *
 * @author Arne Vandamme
 */
@Configuration
@AcrossDepends(required = "AcrossHibernateModule")
public class BasicRepositoryAclInterceptConfiguration
{
	@Bean
	RefreshableRegistry<IdBasedEntityAclInterceptor> aclEntityInterceptors() {
		return new IncrementalRefreshableRegistry<>( IdBasedEntityAclInterceptor.class, true );
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryAclInterceptor aclInterceptor() {
		return new BasicRepositoryAclInterceptor( aclEntityInterceptors() );
	}
}
