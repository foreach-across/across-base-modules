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

package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.context.ExposedBeanDefinition;
import com.foreach.across.core.transformers.ExposedBeanDefinitionTransformer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Checks if the transaction manager (and related beans) being exposed should be configured
 * as primary according to the module settings. The standard {@link AcrossHibernateJpaModule} is primary
 * by default unless it has been explicitly disabled with property {@code acrossHibernate.primary=false}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class PrimaryTransactionManagerTransformer implements ExposedBeanDefinitionTransformer
{
	private final AcrossHibernateJpaModule module;

	@Override
	public void transformBeanDefinitions( Map<String, ExposedBeanDefinition> beanDefinitions ) {
		AcrossHibernateJpaModuleSettings settings = module.getAcrossApplicationContextHolder()
		                                                  .getApplicationContext()
		                                                  .getBean( AcrossHibernateJpaModuleSettings.class );

		if ( isPrimary( settings ) ) {
			LOG.debug( "Registering PlatformTransactionManager, TransactionTemplate and HibernateSessionHolder as primary" );
			beanDefinitions.get( HibernateJpaConfiguration.TRANSACTION_MANAGER ).setPrimary( true );
			beanDefinitions.get( HibernateJpaConfiguration.TRANSACTION_TEMPLATE ).setPrimary( true );
			beanDefinitions.get( "hibernateSessionHolder" ).setPrimary( true );
			beanDefinitions.get( "entityManagerFactory" ).setPrimary( true );

			if ( beanDefinitions.containsKey( "unitOfWork" ) ) {
				beanDefinitions.get( "unitOfWork" ).setPrimary( true );
			}

			ApplicationContext parentContext = module.getAcrossApplicationContextHolder().getApplicationContext().getParent();
			alias( parentContext, beanDefinitions, HibernateJpaConfiguration.TRANSACTION_MANAGER, "transactionManager" );
			alias( parentContext, beanDefinitions, HibernateJpaConfiguration.TRANSACTION_TEMPLATE, "transactionTemplate" );
		}
	}

	private boolean isPrimary( AcrossHibernateJpaModuleSettings settings ) {
		Boolean manuallySetPrimaryStatus = settings.getPrimary();
		return manuallySetPrimaryStatus != null ? manuallySetPrimaryStatus : AcrossHibernateJpaModule.NAME.equals( module.getName() );
	}

	private void alias( ApplicationContext parentContext,
	                    Map<String, ExposedBeanDefinition> beanDefinitions,
	                    String originalBean,
	                    String aliasToAdd ) {
		if ( parentContext.containsBean( aliasToAdd ) ) {
			LOG.debug( "Unable to create '{}' alias for the '{}' - there is already a bean with that name", aliasToAdd, originalBean );
		}
		else {
			beanDefinitions.get( originalBean ).addAlias( aliasToAdd );
		}
	}
}
