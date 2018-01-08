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

package com.foreach.across.modules.hibernate.jpa.config.dynamic;

import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Import(JpaRepositoriesAutoConfiguration.class)
public class JpaRepositoriesRegistrar implements BeanFactoryPostProcessor
{
	@Override
	public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory ) throws BeansException {
		Map<String, PlatformTransactionManager> transactionManagerMap
				= BeanFactoryUtils.beansOfTypeIncludingAncestors( beanFactory, PlatformTransactionManager.class, true, false );

		if ( transactionManagerMap.size() == 1 && transactionManagerMap.containsKey( HibernateJpaConfiguration.TRANSACTION_MANAGER ) ) {
			beanFactory.registerAlias( HibernateJpaConfiguration.TRANSACTION_MANAGER, "transactionManager" );
		}
	}
}
