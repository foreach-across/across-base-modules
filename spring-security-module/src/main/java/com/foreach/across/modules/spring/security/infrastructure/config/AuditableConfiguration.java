package com.foreach.across.modules.spring.security.infrastructure.config;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.spring.security.infrastructure.aop.AuditableEntityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AcrossDepends(required = "AcrossHibernateModule")
public class AuditableConfiguration
{
	@Bean
	public AuditableEntityInterceptor auditableEntityInterceptor() {
		return new AuditableEntityInterceptor();
	}
}
