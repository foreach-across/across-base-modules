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

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.AcrossListableBeanFactory;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Iterator;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JpaModulePropertiesRegistrar implements ImportSelector, BeanFactoryAware, EnvironmentAware
{
	private Environment environment;
	private BeanFactory beanFactory;

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Override
	public void setBeanFactory( BeanFactory beanFactory ) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public String[] selectImports( AnnotationMetadata importingClassMetadata ) {
		AcrossListableBeanFactory lbf = (AcrossListableBeanFactory) beanFactory;

		lbf.registerSingleton(
				JpaModuleProperties.BEAN_NAME,
				new JpaModulePropertiesFactory(
						lbf.getParentBeanFactory().getBean( AcrossContextInfo.class ),
						(AbstractHibernatePackageModule) lbf.getBean( AcrossModule.CURRENT_MODULE ),
						environment,
						lbf.getParentBeanFactory().getBean( ConversionService.class )
				).createInstance()
		);

		return new String[0];
	}

	@RequiredArgsConstructor
	static class JpaModulePropertiesFactory
	{
		private final AcrossContextInfo context;
		private final AbstractHibernatePackageModule currentModule;
		private final Environment environment;
		private final ConversionService conversionService;

		@SneakyThrows
		JpaModuleProperties createInstance() {
			JpaModuleProperties moduleProperties = new JpaModuleProperties();
			PropertySources propertySources = createPropertySources( environment );

			bindProperties( propertySources, "spring.jpa", moduleProperties.getJpaProperties() );
			bindProperties( propertySources, "spring.transaction", moduleProperties.getTransactionProperties() );
			bindProperties( propertySources, "acrossHibernate", moduleProperties.getHibernateModuleSettings() );

			return moduleProperties;
		}

		private void bindProperties( PropertySources propertySources, String prefix, Object target ) throws Exception {
			PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>( target );
			factory.setPropertySources( propertySources );
			factory.setConversionService( conversionService );
			factory.setIgnoreInvalidFields( false );
			factory.setIgnoreNestedProperties( false );
			factory.setIgnoreUnknownFields( true );
			factory.setTargetName( prefix );

			factory.bindPropertiesToTarget();
		}

		/**
		 * Configure the default values based on the context we're running in.
		 * If a dynamic application module is present, and we are the original JPA module as well
		 * as the only one, scan the application module for entities automatically.
		 */
		private void applyDefaultValues( JpaModuleProperties moduleProperties ) {

		}

		private PropertySources createPropertySources( Environment environment ) {
			return new FlatPropertySources( ( (ConfigurableEnvironment) environment ).getPropertySources() );
		}

		/**
		 * Convenience class to flatten out a tree of property sources without losing the
		 * reference to the backing data (which can therefore be updated in the background).
		 */
		private static class FlatPropertySources implements PropertySources
		{
			private PropertySources propertySources;

			FlatPropertySources( PropertySources propertySources ) {
				this.propertySources = propertySources;
			}

			@Override
			public Iterator<PropertySource<?>> iterator() {
				MutablePropertySources result = getFlattened();
				return result.iterator();
			}

			@Override
			public boolean contains( String name ) {
				return get( name ) != null;
			}

			@Override
			public PropertySource<?> get( String name ) {
				return getFlattened().get( name );
			}

			private MutablePropertySources getFlattened() {
				MutablePropertySources result = new MutablePropertySources();
				for ( PropertySource<?> propertySource : this.propertySources ) {
					flattenPropertySources( propertySource, result );
				}
				return result;
			}

			private void flattenPropertySources( PropertySource<?> propertySource,
			                                     MutablePropertySources result ) {
				Object source = propertySource.getSource();
				if ( source instanceof ConfigurableEnvironment ) {
					ConfigurableEnvironment environment = (ConfigurableEnvironment) source;
					for ( PropertySource<?> childSource : environment.getPropertySources() ) {
						flattenPropertySources( childSource, result );
					}
				}
				else {
					result.addLast( propertySource );
				}
			}
		}
	}
}
