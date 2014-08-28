package com.foreach.across.modules.spring.security.acl.config;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptor;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptorAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.PermissionEvaluator;

/**
 * Client module configuration that registers the PermissionEvaluator in all consuming modules.
 *
 * @author Arne Vandamme
 */
@Configuration
public class ModuleAclSecurityConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	PermissionEvaluator permissionEvaluator() {
		return contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityAclModule.NAME, PermissionEvaluator.class );
	}

	@Bean
	@AcrossDepends(required = "AcrossHibernateModule")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryAclInterceptorAdvisor aclInterceptorAdvisor() {
		BasicRepositoryAclInterceptor interceptor = contextBeanRegistry.getBeanOfTypeFromModule(
				SpringSecurityAclModule.NAME, BasicRepositoryAclInterceptor.class
		);

		BasicRepositoryAclInterceptorAdvisor advisor = new BasicRepositoryAclInterceptorAdvisor();
		advisor.setAdvice( interceptor );
		advisor.setOrder( BasicRepositoryAclInterceptorAdvisor.INTERCEPT_ORDER );

		return advisor;
	}
}
