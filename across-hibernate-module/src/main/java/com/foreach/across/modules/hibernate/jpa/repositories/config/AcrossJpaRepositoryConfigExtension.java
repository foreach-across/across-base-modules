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

package com.foreach.across.modules.hibernate.jpa.repositories.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.jpa.util.BeanDefinitionUtils;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.beans.factory.BeanFactoryUtils.transformedBeanName;

/**
 * Custom extension to fix a bug in Spring Data JPA after 1.10.2.
 * Workaround allows the SharedEntityManager to be registered.
 *
 * todo: remove once https://jira.spring.io/browse/DATAJPA-1005 has been fixed
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
class AcrossJpaRepositoryConfigExtension extends JpaRepositoryConfigExtension
{
	private static final String EM_BEAN_DEFINITION_REGISTRAR_POST_PROCESSOR_BEAN_NAME =
			"emBeanDefinitionRegistrarPostProcessor";

	@Override
	public void registerBeansForRoot( BeanDefinitionRegistry registry, RepositoryConfigurationSource config ) {
		registerIfNotAlreadyRegistered(
				new RootBeanDefinition( CustomEntityManagerBeanDefinitionRegistrarPostProcessor.class ),
				registry, EM_BEAN_DEFINITION_REGISTRAR_POST_PROCESSOR_BEAN_NAME, config.getSource() );

		super.registerBeansForRoot( registry, config );

	}

	static class CustomEntityManagerBeanDefinitionRegistrarPostProcessor implements BeanFactoryPostProcessor
	{
		private static final String JNDI_OBJECT_FACTORY_BEAN = "org.springframework.jndi.JndiObjectFactoryBean";
		private static final List<Class<?>> EMF_TYPES;

		static {

			List<Class<?>> types = new ArrayList<Class<?>>();
			types.add( EntityManagerFactory.class );
			types.add( AbstractEntityManagerFactoryBean.class );

			if ( ClassUtils.isPresent( JNDI_OBJECT_FACTORY_BEAN, ClassUtils.getDefaultClassLoader() ) ) {
				types.add( JndiObjectFactoryBean.class );
			}

			EMF_TYPES = Collections.unmodifiableList( types );
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
		 */
		@Override
		public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory ) throws BeansException {

			for ( BeanDefinitionUtils.EntityManagerFactoryBeanDefinition definition :
					getEntityManagerFactoryBeanDefinitions( beanFactory ) ) {

				if ( !( definition.getBeanFactory() instanceof BeanDefinitionRegistry ) ) {
					continue;
				}

				BeanDefinitionBuilder builder = BeanDefinitionBuilder
						.rootBeanDefinition( "org.springframework.orm.jpa.SharedEntityManagerCreator" );
				builder.setFactoryMethod( "createSharedEntityManager" );
				builder.addConstructorArgReference( definition.getBeanName() );

				AbstractBeanDefinition emBeanDefinition = builder.getRawBeanDefinition();

				emBeanDefinition.addQualifier(
						new AutowireCandidateQualifier( Qualifier.class, definition.getBeanName() ) );
				emBeanDefinition.setScope( definition.getBeanDefinition().getScope() );
				emBeanDefinition.setSource( definition.getBeanDefinition().getSource() );

				BeanDefinitionReaderUtils.registerWithGeneratedName( emBeanDefinition,
				                                                     (BeanDefinitionRegistry) definition
						                                                     .getBeanFactory() );
			}
		}

		private static Collection<BeanDefinitionUtils.EntityManagerFactoryBeanDefinition> getEntityManagerFactoryBeanDefinitions(
				ConfigurableListableBeanFactory beanFactory ) {

			List<BeanDefinitionUtils.EntityManagerFactoryBeanDefinition>
					definitions = new ArrayList<BeanDefinitionUtils.EntityManagerFactoryBeanDefinition>();

			for ( Class<?> type : EMF_TYPES ) {

				for ( String name : beanFactory.getBeanNamesForType( type, true, false ) ) {
					registerEntityManagerFactoryBeanDefinition( transformedBeanName( name ), beanFactory, definitions );
				}
			}

			BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();

			if ( parentBeanFactory instanceof ConfigurableListableBeanFactory ) {
				definitions.addAll(
						getEntityManagerFactoryBeanDefinitions( (ConfigurableListableBeanFactory) parentBeanFactory ) );
			}

			return definitions;
		}

		/**
		 * Registers an {@link BeanDefinitionUtils.EntityManagerFactoryBeanDefinition} for the bean with the given name. Drops
		 * {@link JndiObjectFactoryBean} instances that don't point to an {@link EntityManagerFactory} bean as expected type.
		 *
		 * @param name
		 * @param beanFactory
		 * @param definitions
		 */
		private static void registerEntityManagerFactoryBeanDefinition( String name,
		                                                                ConfigurableListableBeanFactory beanFactory,
		                                                                List<BeanDefinitionUtils.EntityManagerFactoryBeanDefinition> definitions ) {

			BeanDefinition definition = beanFactory.getBeanDefinition( name );

			if ( JNDI_OBJECT_FACTORY_BEAN.equals( definition.getBeanClassName() ) ) {
				if ( !EntityManagerFactory.class.getName().equals(
						definition.getPropertyValues().get( "expectedType" ) ) ) {
					return;
				}
				else if ( !EntityManagerFactory.class.equals( beanFactory.getType( name ) ) ) {
					return;
				}
			}

			definitions.add( new BeanDefinitionUtils.EntityManagerFactoryBeanDefinition( name, beanFactory ) );
		}
	}

}
