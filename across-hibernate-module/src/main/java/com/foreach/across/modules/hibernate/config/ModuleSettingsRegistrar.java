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

package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.AcrossListableBeanFactory;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.extensions.HibernatePersistenceContextInViewConfiguration;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Iterator;

/**
 * Creates the {@link com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings} as early as possible,
 * and registers it as a singleton named <strong>moduleSettings</strong> in the {@link  BeanFactory}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ModuleSettingsRegistrar implements ImportSelector, BeanFactoryAware, EnvironmentAware
{
	public static final String BEAN_NAME = "moduleSettings";

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
	public final String[] selectImports( AnnotationMetadata importingClassMetadata ) {
		AcrossListableBeanFactory lbf = (AcrossListableBeanFactory) beanFactory;

		String beanName = AcrossModule.CURRENT_MODULE + "Settings";
		AcrossHibernateModuleSettings moduleSettings = new ModuleSettingsFactory(
				lbf.getParentBeanFactory().getBean( AcrossContextInfo.class ),
				(AbstractHibernatePackageModule) lbf.getBean( AcrossModule.CURRENT_MODULE ),
				environment
		).createInstance();

		if ( !lbf.containsBeanDefinition( beanName ) ) {
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClass( moduleSettings.getClass() );
			beanDefinition.setPrimary( true );
			beanDefinition.addQualifier(
					new AutowireCandidateQualifier( Module.class.getName(), AcrossModule.CURRENT_MODULE )
			);
			lbf.registerBeanDefinition( beanName, beanDefinition );
		}

		lbf.registerSingleton( beanName, moduleSettings );
		lbf.registerAlias( beanName, BEAN_NAME );

		return settingsDependantImports();
	}

	protected String[] settingsDependantImports() {
		return new String[] { HibernatePersistenceContextInViewConfiguration.class.getName() };
	}

	@RequiredArgsConstructor
	static class ModuleSettingsFactory
	{
		private final AcrossContextInfo contextInfo;
		private final AbstractHibernatePackageModule currentModule;
		private final Environment environment;
		private final DefaultConversionService conversionService = new DefaultConversionService();

		@SneakyThrows
		AcrossHibernateModuleSettings createInstance() {
			AcrossHibernateModuleSettings moduleSettings = currentModule.createSettings();
			applyDefaultValues( moduleSettings );

			PropertySources propertySources = createPropertySources( environment );

			if ( isDefaultHibernateModule() ) {
				bindProperties( propertySources, "spring.jpa", moduleSettings );
				bindProperties( propertySources, "spring.transaction", moduleSettings.getTransactionProperties() );
				moduleSettings.getApplicationModule().setRepositoryScan(
						environment.getProperty( "spring.data.jpa.repositories.enabled", boolean.class,
						                         moduleSettings.getApplicationModule().isRepositoryScan() )
				);
			}

			String modulePrefix = currentModule.getPropertiesPrefix();
			bindProperties( propertySources, modulePrefix, moduleSettings );
			bindProperties( propertySources, modulePrefix + ".transaction", moduleSettings.getTransactionProperties() );
			bindProperties( propertySources, modulePrefix + ".application", moduleSettings.getApplicationModule() );

			return moduleSettings;
		}

		private void bindProperties( PropertySources propertySources, String prefix, Object target ) throws Exception {
			Binder binder = new Binder(
					ConfigurationPropertySources.from( propertySources ),
					new PropertySourcesPlaceholdersResolver( propertySources ),
					conversionService
			);

			binder.bind( prefix, Bindable.ofInstance( target ) );

			/*
			PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>( target );
			factory.setPropertySources( propertySources );
			factory.setConversionService( conversionService );
			factory.setIgnoreInvalidFields( false );
			factory.setIgnoreNestedProperties( false );
			factory.setIgnoreUnknownFields( true );
			factory.setTargetName( prefix );

			factory.bindPropertiesToTarget();*/
		}

		/**
		 * Configure the default values based on the context we're running in.
		 * If a dynamic application module is present, and we are the original JPA module as well
		 * as the only one, scan the application module for entities automatically.
		 */
		private void applyDefaultValues( AcrossHibernateModuleSettings moduleSettings ) {
			boolean scanDefaults = isDefaultHibernateModule() && isSingleHibernateModule();

			moduleSettings.setDataSource( currentModule.getDataSourceName() );
			moduleSettings.getApplicationModule().setEntityScan( scanDefaults );
			moduleSettings.getApplicationModule().setRepositoryScan( scanDefaults );
			moduleSettings.setGenerateDdl( false );
			moduleSettings.getHibernate().setDdlAuto( "none" );

			if ( moduleSettings instanceof AcrossHibernateJpaModuleSettings ) {
				final AcrossHibernateJpaModuleSettings jpaModuleSettings = (AcrossHibernateJpaModuleSettings) moduleSettings;
				jpaModuleSettings.setPersistenceUnitName( currentModule.getName() );

				if ( isSingleHibernateModule()
						&& BeanFactoryUtils.beansOfTypeIncludingAncestors( contextInfo.getApplicationContext(),
						                                                   PlatformTransactionManager.class ).isEmpty() ) {
					LOG.trace(
							"Switching to default primary as this is the only AcrossHibernateJpaModule and there are no other transaction managers" );
					jpaModuleSettings.setPrimary( true );
				}
			}
		}

		private boolean isDefaultHibernateModule() {
			return AcrossHibernateJpaModule.NAME.equals( currentModule.getName() );
		}

		private boolean isSingleHibernateModule() {
			return contextInfo.getModules()
			                  .stream()
			                  .map( AcrossModuleInfo::getModule )
			                  .filter( AbstractHibernatePackageModule.class::isInstance )
			                  .count() == 1;
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
