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
import com.foreach.across.core.transformers.BeanDefinitionTransformerComposite;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;
import com.foreach.across.modules.hibernate.jpa.config.PrimaryTransactionManagerTransformer;

import javax.sql.DataSource;

/**
 * Enables JPA support using Hibernate as the implementation.
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(optional = "EhcacheModule")
public class AcrossHibernateJpaModule extends AbstractHibernatePackageModule
{
	public static final String NAME = "AcrossHibernateJpaModule";

	public AcrossHibernateJpaModule() {
		setHibernateProperty( "hibernate.cache.use_second_level_cache", "false" );
		setPersistenceUnitName( getName() );
		setPropertiesPrefix( NAME.equals( getName() ) ? "acrossHibernate" : null );
	}

	public AcrossHibernateJpaModule( DataSource dataSource ) {
		this();
		setDataSource( dataSource );
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Enables JPA support on the Across context using Hibernate as the vendor implementation.";
	}

	protected void setPersistenceUnitName( String persistenceUnitName ) {
		setProperty( AcrossHibernateJpaModuleSettings.PERSISTENCE_UNIT_NAME, persistenceUnitName );
	}

	/**
	 * Configure this module as primary: makes some exposed beans primary and will attempt to register aliases
	 * for the transaction manager and transaction template.
	 *
	 * @param primary or not
	 */
	public void setPrimary( Boolean primary ) {
		setProperty( AcrossHibernateJpaModuleSettings.PRIMARY, primary );
	}

	@Override
	public AcrossHibernateJpaModuleSettings createSettings() {
		return new AcrossHibernateJpaModuleSettings();
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		if ( getName().equals( currentModule.getModuleName() ) ) {
			if ( currentModule.getExposeTransformer() != null ) {
				currentModule.setExposeTransformer(
						new BeanDefinitionTransformerComposite( currentModule.getExposeTransformer(), new PrimaryTransactionManagerTransformer( this ) )
				);
			}
			else {
				currentModule.setExposeTransformer( new PrimaryTransactionManagerTransformer( this ) );
			}
		}
	}
}
