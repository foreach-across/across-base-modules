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

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestAcrossHibernateJpaModuleBuilder
{
	@Test
	public void allGeneratedValues() {
		AcrossHibernateJpaModule module = AcrossHibernateJpaModule
				.builder()
				.prefix( "foo" )
				.build();

		assertThat( module ).isNotNull();
		assertThat( module.getName() ).isEqualTo( "FooJpaModule" );
		assertThat( module.getPropertiesPrefix() ).isEqualTo( "foo.jpa" );
		assertThat( module.getDataSourceName() ).isEqualTo( "fooDataSource" );
		assertThat( module.getProperties() ).containsEntry( "foo.jpa.primary", false );
		assertThat( module.getProperties() ).containsEntry( "foo.jpa.persistenceUnitName", "FooJpaModule" );
		assertThat( module.getProperties() ).containsEntry( "foo.jpa.hibernateProperties",
		                                                    Collections.singletonMap( "hibernate.cache.use_second_level_cache", "false" ) );
	}

	@Test
	public void allFixedValues() {
		AcrossHibernateJpaModule module = AcrossHibernateJpaModule
				.builder()
				.moduleName( "MyConnectorModule" )
				.exposePrefix( "my" )
				.propertiesPrefix( "connector" )
				.dataSourceName( "myDataSource" )
				.primary( true )
				.persistenceUnitName( "my-entities" )
				.build();

		assertThat( module ).isNotNull();
		assertThat( module.getName() ).isEqualTo( "MyConnectorModule" );
		assertThat( module.getPropertiesPrefix() ).isEqualTo( "connector" );
		assertThat( module.getDataSourceName() ).isEqualTo( "myDataSource" );
		assertThat( module.getProperties() ).containsEntry( "connector.primary", true );
		assertThat( module.getProperties() ).containsEntry( "connector.persistenceUnitName", "my-entities" );
		assertThat( module.getProperties() ).containsEntry( "connector.hibernateProperties",
		                                                    Collections.singletonMap( "hibernate.cache.use_second_level_cache", "false" ) );
	}
}
