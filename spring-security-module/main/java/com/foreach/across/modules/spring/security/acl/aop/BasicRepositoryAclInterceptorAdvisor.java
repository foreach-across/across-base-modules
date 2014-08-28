package com.foreach.across.modules.spring.security.acl.aop;

import com.foreach.across.core.context.configurer.TransactionManagementConfigurer;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author Arne Vandamme
 */
public class BasicRepositoryAclInterceptorAdvisor extends AbstractBeanFactoryPointcutAdvisor
{
	/**
	 * By default the interceptor should run within the same transaction.
	 */
	public static final int INTERCEPT_ORDER = TransactionManagementConfigurer.INTERCEPT_ORDER + 1;

	private final Pointcut pointcut = new BasicRepositoryAclPointcut();

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}
}
