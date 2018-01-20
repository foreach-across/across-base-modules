package com.foreach.across.modules.spring.security.infrastructure.config;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.spring.security.infrastructure.aop.AuditableEntityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnAcrossModule(anyOf = { "AcrossHibernateModule", "AcrossHibernateJpaModule" })
public class AuditableConfiguration
{
	@Bean
	public AuditableEntityInterceptor auditableEntityInterceptor() {
		return new AuditableEntityInterceptor();
	}
}
