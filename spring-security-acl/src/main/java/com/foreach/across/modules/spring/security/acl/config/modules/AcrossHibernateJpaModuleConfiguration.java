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
package com.foreach.across.modules.spring.security.acl.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.aop.AclSecurityEntityAclInterceptor;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configures intercepting the BasicRepository methods to create or delete ACLs when an entity
 * gets inserted/updated/deleted.
 * <p/>
 * Also enables the {@link com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity}.
 *
 * @author Arne Vandamme
 */
@Configuration
@AcrossDepends(required = "AcrossHibernateJpaModule")
@EnableJpaRepositories(
		transactionManagerRef = HibernateJpaConfiguration.TRANSACTION_MANAGER,
		basePackageClasses = SpringSecurityAclModule.class
)
public class AcrossHibernateJpaModuleConfiguration
{
	@Bean
	public AclSecurityEntityService aclSecurityEntityService() {
		return new AclSecurityEntityServiceImpl();
	}

	@Bean
	public AclSecurityEntityAclInterceptor aclSecurityEntityAclInterceptor() {
		return new AclSecurityEntityAclInterceptor();
	}
}
