package com.foreach.across.modules.spring.security.acl.config;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
}
