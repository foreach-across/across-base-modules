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

package com.foreach.across.modules.hibernate.modules.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.foreach.across.modules.hibernate.modules.config.EnableTransactionManagementConfiguration.INTERCEPT_ORDER;

/**
 * Enables transaction management in modules.
 * Uses a custom interceptor order and does not inspect aop type property,
 * unlike {@link org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration}.
 * <p/>
 * If a module wants to use target class proxying by default, it should specify its own transaction management approach.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
@ConditionalOnBean(PlatformTransactionManager.class)
@ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
@EnableTransactionManagement(order = INTERCEPT_ORDER)
public class EnableTransactionManagementConfiguration
{
	/**
	 * Order for the AOP interceptor.
	 */
	public static final int INTERCEPT_ORDER = Ordered.LOWEST_PRECEDENCE - 10;

}
