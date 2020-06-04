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

package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.core.transformers.BeanDefinitionTransformerComposite;
import com.foreach.across.core.transformers.BeanPrefixingTransformer;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import com.foreach.across.modules.hibernate.jpa.config.PrimaryTransactionManagerTransformer;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Set;

/**
 * Enables JPA support using Hibernate as the implementation.
 */
@Slf4j
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(optional = "EhcacheModule")
public class AcrossHibernateJpaModule extends AbstractHibernatePackageModule
{
	public static final String NAME = "AcrossHibernateJpaModule";

	private String moduleName = NAME;

	public AcrossHibernateJpaModule() {
		setPropertiesPrefix( NAME.equals( getName() ) ? "across-hibernate" : null );
		setPersistenceUnitName( getName() );
		setHibernateProperty( "hibernate.cache.use_second_level_cache", "false" );
	}

	public AcrossHibernateJpaModule( DataSource dataSource ) {
		this();
		setDataSource( dataSource );
	}

	@Override
	public String getName() {
		return moduleName;
	}

	/**
	 * Set a custom module name, only exposed for the {@link AcrossHibernateJpaModuleBuilder}.
	 */
	void setModuleName( String moduleName ) {
		this.moduleName = moduleName;
	}

	@Override
	public String getDescription() {
		return "Enables JPA support on the Across context using Hibernate as the vendor implementation.";
	}

	protected final void setPersistenceUnitName( String persistenceUnitName ) {
		setProperty( getPropertiesPrefix() + ".persistenceUnitName", persistenceUnitName );
	}

	/**
	 * Configure this module as primary: makes some exposed beans primary and will attempt to register aliases
	 * for the transaction manager and transaction template.
	 *
	 * @param primary or not
	 */
	public final void setPrimary( Boolean primary ) {
		if ( primary != null ) {
			setProperty( getPropertiesPrefix() + ".primary", primary );
		}
		else {
			getProperties().remove( getPropertiesPrefix() + ".primary" );
		}
	}

	@Override
	public AcrossHibernateJpaModuleSettings createSettings() {
		return new AcrossHibernateJpaModuleSettings();
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( new ComponentScanConfigurer( AcrossHibernateJpaModule.class.getPackage().getName() + ".config" ) );
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		if ( getName().equals( currentModule.getModuleName() ) ) {
			if ( currentModule.getExposeTransformer() != null ) {
				currentModule.setExposeTransformer(
						new BeanDefinitionTransformerComposite( currentModule.getExposeTransformer(),
						                                        new PrimaryTransactionManagerTransformer( this ) )
				);
			}
			else {
				currentModule.setExposeTransformer( new PrimaryTransactionManagerTransformer( this ) );
			}
		}
	}

	/**
	 * @return a builder for a custom {@link AcrossHibernateJpaModule}
	 */
	public static AcrossHibernateJpaModuleBuilder builder() {
		return new AcrossHibernateJpaModuleBuilder();//AcrossHibernateJpaModule.builder().moduleName( "kiekeboe" ).dataSource( name ).propertiesPrefix("boe.me").exposePrefix( "meh" ).primary( true ).build();
	}

	/**
	 * Builder for creating a custom {@link AcrossHibernateJpaModule}.
	 * If you only specify a {@link #prefix} all other values will get generated defaults.
	 * Consider prefix <strong>foo</strong>:
	 * <ul>
	 * <li>module name would be <strong>FooJpaModule</strong></li>
	 * <li>properties would be prefixed with <strong>foo.jpa</strong></li>
	 * <li>datasource bean with name <strong>fooDataSource</strong> would be looked for</li>
	 * <li>created beans would be exposed with a <strong>foo</strong> prefix, fi the transaction manager bean
	 * would be accessible as <strong>fooJpaTransactionManager</strong></li>
	 * </ul>
	 */
	@Accessors(fluent = true)
	public static class AcrossHibernateJpaModuleBuilder
	{
		/**
		 * General prefix with which to create defaults for fields not explicitly set.
		 */
		@Setter
		private String prefix;

		/**
		 * Name of the generated module.
		 */
		@Setter
		private String moduleName;

		/**
		 * Bean name prefix to apply to the beans being exposed.
		 */
		@Setter
		private String exposePrefix;

		/**
		 * Prefix that all properties for this module should have.
		 */
		@Setter
		private String propertiesPrefix;

		/**
		 * Should the exposed beans from this module be marked as primary.
		 */
		@Setter
		private boolean primary;

		/**
		 * Datasource instance that the entity manager should use.
		 */
		@Setter
		private DataSource dataSource;

		/**
		 * Name of the datasource bean the entity manager should use.
		 */
		@Setter
		private String dataSourceName;

		/**
		 * Custom name of the persistence unit that is being managed.
		 */
		@Setter
		private String persistenceUnitName;

		/**
		 * @return built module
		 */
		public AcrossHibernateJpaModule build() {
			AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();

			String prefix = this.prefix;
			String propertiesSuffix = ".jpa";

			if ( prefix == null ) {
				LOG.trace( "No module prefix configured, attempting to use module name as prefix: {}", this.moduleName );
				prefix = this.moduleName;
				propertiesSuffix = "";
			}

			if ( prefix == null ) {
				throw new IllegalArgumentException( "You need a default prefix or module name to generate an AcrossHibernateJpaModule" );
			}

			module.setModuleName( StringUtils.defaultString( this.moduleName, StringUtils.capitalize( prefix + "JpaModule" ) ) );
			module.setPropertiesPrefix(
					StringUtils.defaultString( this.propertiesPrefix, StringUtils.uncapitalize( prefix ) + propertiesSuffix ) );
			module.setDataSource( dataSource );
			module.setDataSourceName( StringUtils.defaultString( dataSourceName, StringUtils.uncapitalize( prefix + "DataSource" ) ) );
			module.setPersistenceUnitName( StringUtils.defaultString( persistenceUnitName, module.getName() ) );
			module.setPrimary( primary );
			String actualExposePrefix = StringUtils.defaultString( exposePrefix, StringUtils.uncapitalize( prefix ) );
			module.setExposeTransformer( new BeanPrefixingTransformer( actualExposePrefix ) );

			LOG.info( "Built AcrossHibernateJpaModule with name: {}, properties prefix: {}, expose prefix: {}, data source bean: {}",
			          module.getName(), module.getPropertiesPrefix(), actualExposePrefix, module.getDataSourceName() );

			return module;
		}
	}
}
