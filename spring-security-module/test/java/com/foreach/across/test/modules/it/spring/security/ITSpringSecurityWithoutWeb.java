package com.foreach.across.test.modules.it.spring.security;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITSpringSecurityWithoutWeb.Config.class)
public class ITSpringSecurityWithoutWeb
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Autowired(required = false)
	private AclSecurityService aclSecurityService;

	@Autowired(required = false)
	private SecurityPrincipalService securityPrincipalService;

	@Test
	public void authenticationManagerBuilderShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME,
		                                                            AuthenticationManagerBuilder.class ) );
	}

	@Test
	public void aclServiceShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityAclModule.NAME,
		                                                            MutableAclService.class ) );
		assertNotNull( aclSecurityService );
	}

	@Test
	public void securityPrincipalServiceShouldExist() {
		assertNotNull( securityPrincipalService );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( springSecurityModule() );
		}

		private SpringSecurityModule springSecurityModule() {
			return new SpringSecurityModule();
		}
	}
}
