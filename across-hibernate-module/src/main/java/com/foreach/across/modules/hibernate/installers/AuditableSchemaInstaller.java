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
package com.foreach.across.modules.hibernate.installers;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.InstallerGroup;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.support.ModuleBeanSelectorUtils;
import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * <p>
 * Convenience installer that creates the necessary columns for the default implementation
 * of the {@link com.foreach.across.modules.hibernate.business.Auditable} interface.
 * Supports multiple table names and will add the auditable columns to each of those tables.
 * </p>
 *
 * @author Andy Somers, Arne Vandamme
 * @see AcrossLiquibaseInstaller
 * @see com.foreach.across.modules.hibernate.business.AuditableEntity
 */
@InstallerGroup(InstallerGroup.SCHEMA)
public abstract class AuditableSchemaInstaller
{
	private static final String CHANGELOG =
			"classpath:com/foreach/across/modules/hibernate/installers/AuditableSchemaInstaller.xml";

	private static final Logger LOG = LoggerFactory.getLogger( AuditableSchemaInstaller.class );

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossModuleInfo moduleInfo;

	@Autowired
	@Qualifier(AcrossContext.INSTALLER_DATASOURCE)
	private DataSource dataSource;

	private SchemaConfiguration schemaConfiguration;
	private String defaultSchema;

	public AuditableSchemaInstaller() {
	}

	protected AuditableSchemaInstaller( SchemaConfiguration schemaConfiguration ) {
		this.schemaConfiguration = schemaConfiguration;
	}

	protected abstract Collection<String> getTableNames();

	protected SchemaConfiguration getSchemaConfiguration() {
		return schemaConfiguration;
	}

	protected void setSchemaConfiguration( SchemaConfiguration schemaConfiguration ) {
		this.schemaConfiguration = schemaConfiguration;
	}

	/**
	 * Sets the default Schema that will be used during the liquibase update
	 * <p>
	 * This will override the defaultSchema configured in {@link SchemaConfiguration#getDefaultSchema()}
	 *
	 * @param defaultSchema The default db schema name
	 * @see liquibase.integration.spring.SpringLiquibase#setDefaultSchema(String)
	 */
	protected void setDefaultSchema( String defaultSchema ) {
		this.defaultSchema = defaultSchema;
	}

	/**
	 * @return The db schema name that will be used as default schema during the liquibase update
	 */
	protected String getDefaultSchema() {
		return defaultSchema;
	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Override the dataSource this installer should use (defaults to the installer datasource otherwise).
	 *
	 * @param dataSource instance
	 */
	protected void setDataSource( DataSource dataSource ) {
		this.dataSource = dataSource;
	}

	@Autowired
	protected void setBeanFactory( ConfigurableListableBeanFactory beanFactory ) {
		if ( schemaConfiguration == null ) {
			Optional<SchemaConfiguration> schemaConfigurationOptional = ModuleBeanSelectorUtils
					.selectBeanForModule( SchemaConfiguration.class, moduleInfo.getName(), beanFactory );
			schemaConfigurationOptional.ifPresent( this::setSchemaConfiguration );
		}
	}

	@InstallerMethod
	public void install() throws LiquibaseException {
		for ( String tableName : getTableNames() ) {
			String tableNameToUse = schemaConfiguration != null
					? schemaConfiguration.getCurrentTableName( tableName ) : tableName;

			SpringLiquibase liquibase = new SpringLiquibase();
			if ( liquibase.getResourceLoader() == null ) {
				liquibase.setResourceLoader( applicationContext );
			}
			liquibase.setChangeLog( CHANGELOG );

			if ( StringUtils.isNotEmpty( getDefaultSchema() ) ) {
				liquibase.setDefaultSchema( getDefaultSchema() );
			}
			else if ( getSchemaConfiguration() != null ) {
				liquibase.setDefaultSchema( getSchemaConfiguration().getDefaultSchema() );
			}

			liquibase.setDataSource( dataSource );
			liquibase.setChangeLogParameters( Collections.singletonMap( "table.auditable_table", tableNameToUse ) );

			LOG.debug( "Installing auditable columns for table {}", tableNameToUse );

			liquibase.afterPropertiesSet();
		}
	}
}
