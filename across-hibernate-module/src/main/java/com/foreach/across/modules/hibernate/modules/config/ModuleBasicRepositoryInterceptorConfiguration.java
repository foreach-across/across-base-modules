package com.foreach.across.modules.hibernate.modules.config;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptor;
import com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptorAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class ModuleBasicRepositoryInterceptorConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryInterceptorAdvisor aclInterceptorAdvisor() {
		BasicRepositoryInterceptor interceptor = contextBeanRegistry.getBeanOfTypeFromModule(
				AcrossHibernateModule.NAME, BasicRepositoryInterceptor.class
		);

		BasicRepositoryInterceptorAdvisor advisor = new BasicRepositoryInterceptorAdvisor();
		advisor.setAdvice( interceptor );
		advisor.setOrder( BasicRepositoryInterceptorAdvisor.INTERCEPT_ORDER );

		return advisor;
	}
}
