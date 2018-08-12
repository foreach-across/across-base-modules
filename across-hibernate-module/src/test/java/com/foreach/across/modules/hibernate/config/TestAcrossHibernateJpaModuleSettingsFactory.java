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

import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestAcrossHibernateJpaModuleSettingsFactory
{
	private AcrossContextInfo contextInfo = mock( AcrossContextInfo.class );
	private AbstractHibernatePackageModule currentModule = mock( AbstractHibernatePackageModule.class );
	private ConfigurableEnvironment environment = new StandardEnvironment();

	private AcrossModuleInfo currentModuleInfo;

	private ModuleSettingsRegistrar.ModuleSettingsFactory propertiesFactory
			= new ModuleSettingsRegistrar.ModuleSettingsFactory( contextInfo, currentModule, environment );

	@Before
	public void reset() {
		currentModuleInfo = mock( AcrossModuleInfo.class );
		when( currentModuleInfo.getName() ).thenReturn( AcrossHibernateJpaModule.NAME );
		when( currentModuleInfo.getModule() ).thenReturn( currentModule );
		when( currentModule.getDataSourceName() ).thenReturn( "myDataSource" );

		when( contextInfo.getApplicationContext() ).thenReturn( mock( ApplicationContext.class ) );
		when( contextInfo.getModules() ).thenReturn( Collections.singletonList( currentModuleInfo ) );
		when( currentModule.getName() ).thenReturn( AcrossHibernateJpaModule.NAME );
		when( currentModule.getPropertiesPrefix() ).thenReturn( "across.hibernate" );
		when( currentModule.createSettings() ).thenReturn( new AcrossHibernateJpaModuleSettings() );
	}

	@Test
	public void defaultValuesIfSingleModule() {
		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getPersistenceUnitName() ).isEqualTo( AcrossHibernateJpaModule.NAME );
		assertThat( properties.getApplicationModule().isEntityScan() ).isTrue();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isTrue();
		assertThat( properties.isGenerateDdl() ).isFalse();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "none" );
		assertThat( properties.getDataSource() ).isEqualTo( "myDataSource" );

		assertThat( properties.getHibernateProperties( new HibernateSettings() ) )
				.doesNotContainKey( "hibernate.hbm2ddl.auto" );

		assertThat( properties.getPrimary() ).isTrue();
		assertThat( properties.isShowSql() ).isFalse();
		assertThat( properties.isOpenInView() ).isTrue();
		assertThat( properties.getTransactionProperties().getDefaultTimeout() ).isNull();
		assertThat( properties.getTransactionProperties().getRollbackOnCommitFailure() ).isNull();
		assertThat( properties.isCreateUnitOfWorkFactory() ).isFalse();
		assertThat( properties.getPersistenceContextInView().getHandler() ).isEqualTo( PersistenceContextInView.FILTER );
	}

	@Test
	public void applicationModuleScanDisabledIfMultipleHibernateModules() {
		AbstractHibernatePackageModule otherModule = mock( AbstractHibernatePackageModule.class );
		when( otherModule.getName() ).thenReturn( "MyOtherHibernateModule" );

		AcrossModuleInfo otherModuleInfo = mock( AcrossModuleInfo.class );
		when( otherModuleInfo.getName() ).thenReturn( "MyOtherHibernateModule" );
		when( otherModuleInfo.getModule() ).thenReturn( otherModule );

		when( contextInfo.getModules() ).thenReturn( Arrays.asList( currentModuleInfo, otherModuleInfo ) );

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getPrimary() ).isNull();
		assertThat( properties.getApplicationModule().isEntityScan() ).isFalse();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
		assertThat( properties.isGenerateDdl() ).isFalse();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "none" );
	}

	@Test
	public void applicationModuleScanDisabledIfNotDefaultModule() {
		when( currentModule.getName() ).thenReturn( "OtherModule" );
		when( currentModule.getPropertiesPrefix() ).thenReturn( "other" );

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getApplicationModule().isEntityScan() ).isFalse();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
		assertThat( properties.isGenerateDdl() ).isFalse();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "none" );
	}

	@Test
	public void applicationModuleScanExplicitlyEnabledIfMultipleModules() {
		AbstractHibernatePackageModule otherModule = mock( AbstractHibernatePackageModule.class );
		when( otherModule.getName() ).thenReturn( "MyOtherHibernateModule" );

		AcrossModuleInfo otherModuleInfo = mock( AcrossModuleInfo.class );
		when( otherModuleInfo.getName() ).thenReturn( "MyOtherHibernateModule" );
		when( otherModuleInfo.getModule() ).thenReturn( otherModule );

		when( contextInfo.getModules() ).thenReturn( Arrays.asList( currentModuleInfo, otherModuleInfo ) );

		properties( "across.hibernate.application.entity-scan=true", "across.hibernate.application.repository-scan=true" );

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getApplicationModule().isEntityScan() ).isTrue();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isTrue();
		assertThat( properties.isGenerateDdl() ).isFalse();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "none" );
	}

	@Test
	public void applicationModuleScanExplicitlyEnabledIfNotDefaultModule() {
		properties( "other.application.entity-scan=true", "other.application.repository-scan=true" );

		when( currentModule.getName() ).thenReturn( "OtherModule" );
		when( currentModule.getPropertiesPrefix() ).thenReturn( "other" );

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getApplicationModule().isEntityScan() ).isTrue();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isTrue();
		assertThat( properties.isGenerateDdl() ).isFalse();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "none" );
	}

	@Test
	public void defaultModuleSpringBootPropertyBinding() {
		MapPropertySource mp = new MapPropertySource(
				"moduleProperties",
				Collections.singletonMap(
						"across.hibernate.hibernateProperties",
						new HashMap<>( Collections.singletonMap( "other-property", "other-value" ) )
				)
		);
		environment.getPropertySources().addFirst( mp );

		properties(
				"across.hibernate.data-source=some-ds",
				"across.hibernate.application.entity-scan=false",
				"across.hibernate.application.repository-scan=false",
				"spring.jpa.generate-ddl=true",
				"spring.jpa.hibernate.ddl-auto=create-drop",
				"spring.jpa.properties.key=value",
				"spring.jpa.show-sql=true",
				"spring.jpa.open-in-view=false",
				"spring.transaction.default-timeout=66",
				"spring.transaction.rollback-on-commit-failure=true",
				"across.hibernate.create-unit-of-work-factory=true",
				"across.hibernate.persistenceContextInView.handler=INTERCEPTOR",
				"across.hibernate.hibernateProperties[hibernate.hbm2ddl.auto]=update",
				"spring.data.jpa.repositories.enabled=true"
		);

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getApplicationModule().isEntityScan() ).isFalse();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
		assertThat( properties.isGenerateDdl() ).isTrue();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "create-drop" );
		assertThat( properties.isShowSql() ).isTrue();
		assertThat( properties.isOpenInView() ).isFalse();
		assertThat( properties.getTransactionProperties().getDefaultTimeout() ).isEqualTo( 66 );
		assertThat( properties.getTransactionProperties().getRollbackOnCommitFailure() ).isTrue();
		assertThat( properties.isCreateUnitOfWorkFactory() ).isTrue();
		assertThat( properties.getPersistenceContextInView().getHandler() ).isEqualTo( PersistenceContextInView.INTERCEPTOR );
		assertThat( properties.getDataSource() ).isEqualTo( "some-ds" );

		assertThat( properties.getHibernateProperties( new HibernateSettings() ) )
				.containsEntry( "key", "value" )
				.containsEntry( "other-property", "other-value" )
				.containsEntry( "hibernate.hbm2ddl.auto", "update" );
	}

	@Test
	public void defaultModulePropertyBinding() {
		properties(
				"across.hibernate.application.entity-scan=false",
				"across.hibernate.application.repository-scan=false",
				"across.hibernate.generate-ddl=true",
				"across.hibernate.hibernate.ddl-auto=create-drop",
				"across.hibernate.properties.key=value",
				"across.hibernate.show-sql=true",
				"across.hibernate.open-in-view=false",
				"across.hibernate.transaction.default-timeout=66",
				"across.hibernate.transaction.rollback-on-commit-failure=true",
				"across.hibernate.create-unit-of-work-factory=true",
				"across.hibernate.persistenceContextInView.handler=INTERCEPTOR",
				"across.hibernate.hibernateProperties[hibernate.hbm2ddl.auto]=update"
		);

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getApplicationModule().isEntityScan() ).isFalse();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
		assertThat( properties.isGenerateDdl() ).isTrue();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "create-drop" );
		assertThat( properties.isShowSql() ).isTrue();
		assertThat( properties.isOpenInView() ).isFalse();
		assertThat( properties.getTransactionProperties().getDefaultTimeout() ).isEqualTo( 66 );
		assertThat( properties.getTransactionProperties().getRollbackOnCommitFailure() ).isTrue();
		assertThat( properties.isCreateUnitOfWorkFactory() ).isTrue();
		assertThat( properties.getPersistenceContextInView().getHandler() ).isEqualTo( PersistenceContextInView.INTERCEPTOR );

		assertThat( properties.getHibernateProperties( new HibernateSettings() ) )
				.containsEntry( "key", "value" )
				.containsEntry( "hibernate.hbm2ddl.auto", "update" );
	}

	@Test
	public void defaultModuleChecksDefaultRepositoriesProperty() {
		properties( "spring.data.jpa.repositories.enabled=false" );

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getApplicationModule().isEntityScan() ).isTrue();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
	}

	@Test
	public void customModuleIgnoresDefaultProperties() {
		when( currentModule.getName() ).thenReturn( "OtherModule" );
		when( currentModule.getPropertiesPrefix() ).thenReturn( "other" );

		properties(
				"across.hibernate.application.entity-scan=false",
				"across.hibernate.application.repository-scan=false",
				"spring.jpa.generate-ddl=true",
				"spring.jpa.hibernate.ddl-auto=create-drop",
				"spring.jpa.properties.key=value",
				"spring.jpa.show-sql=true",
				"spring.jpa.open-in-view=false",
				"spring.transaction.default-timeout=66",
				"spring.transaction.rollback-on-commit-failure=true",
				"across.hibernate.create-unit-of-work-factory=true",
				"across.hibernate.persistenceContextInView.handler=INTERCEPTOR",
				"across.hibernate.hibernateProperties[hibernate.hbm2ddl.auto]=update",
				"spring.data.jpa.repositories.enabled=true"
		);

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getPersistenceUnitName() ).isEqualTo( "OtherModule" );
		assertThat( properties.getApplicationModule().isEntityScan() ).isFalse();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
		assertThat( properties.isGenerateDdl() ).isFalse();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "none" );

		assertThat( properties.getHibernateProperties( new HibernateSettings() ) )
				.doesNotContainKey( "hibernate.hbm2ddl.auto" );

		assertThat( properties.isShowSql() ).isFalse();
		assertThat( properties.isOpenInView() ).isTrue();
		assertThat( properties.getTransactionProperties().getDefaultTimeout() ).isNull();
		assertThat( properties.getTransactionProperties().getRollbackOnCommitFailure() ).isNull();
		assertThat( properties.isCreateUnitOfWorkFactory() ).isFalse();
		assertThat( properties.getPersistenceContextInView().getHandler() ).isEqualTo( PersistenceContextInView.FILTER );
	}

	@Test
	public void customModulePropertyBinding() {
		when( currentModule.getName() ).thenReturn( "OtherModule" );
		when( currentModule.getPropertiesPrefix() ).thenReturn( "other" );

		properties(
				"other.application.entity-scan=false",
				"other.application.repository-scan=false",
				"other.generate-ddl=true",
				"other.hibernate.ddl-auto=create-drop",
				"other.properties.key=value",
				"other.show-sql=true",
				"other.open-in-view=false",
				"other.transaction.default-timeout=66",
				"other.transaction.rollback-on-commit-failure=true",
				"other.create-unit-of-work-factory=true",
				"other.persistenceContextInView.handler=INTERCEPTOR",
				"other.hibernateProperties[hibernate.hbm2ddl.auto]=update"
		);

		AcrossHibernateJpaModuleSettings properties = (AcrossHibernateJpaModuleSettings) propertiesFactory.createInstance();
		assertThat( properties.getPersistenceUnitName() ).isEqualTo( "OtherModule" );
		assertThat( properties.getApplicationModule().isEntityScan() ).isFalse();
		assertThat( properties.getApplicationModule().isRepositoryScan() ).isFalse();
		assertThat( properties.isGenerateDdl() ).isTrue();
		assertThat( properties.getHibernate().getDdlAuto() ).isEqualTo( "create-drop" );
		assertThat( properties.isShowSql() ).isTrue();
		assertThat( properties.isOpenInView() ).isFalse();
		assertThat( properties.getTransactionProperties().getDefaultTimeout() ).isEqualTo( 66 );
		assertThat( properties.getTransactionProperties().getRollbackOnCommitFailure() ).isTrue();
		assertThat( properties.isCreateUnitOfWorkFactory() ).isTrue();
		assertThat( properties.getPersistenceContextInView().getHandler() ).isEqualTo( PersistenceContextInView.INTERCEPTOR );

		assertThat( properties.getHibernateProperties( new HibernateSettings() ) )
				.containsEntry( "key", "value" )
				.containsEntry( "hibernate.hbm2ddl.auto", "update" );
	}

	private void properties( String... properties ) {
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment( environment, properties );
	}
}
