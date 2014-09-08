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
