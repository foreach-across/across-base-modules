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

package test.boot.apps.multiple;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import test.boot.apps.multiple.connector.CustomConnectorModule;
import test.boot.apps.multiple.entities.EntitiesModule;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
@AcrossApplication(modules = { AcrossHibernateJpaModule.NAME, CustomConnectorModule.NAME })
public class MultipleDataSourceApplication
{
	@Bean
	@Primary
	@ConfigurationProperties("app.datasource.foo")
	public DataSourceProperties fooDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties("app.datasource.foo")
	public DataSource acrossDataSource() {
		return fooDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean
	@ConfigurationProperties("app.datasource.bar")
	public DataSourceProperties barDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("app.datasource.bar")
	public DataSource barDataSource() {
		return barDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean
	@ConfigurationProperties("app.datasource.my")
	public DataSourceProperties myDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("app.datasource.my")
	public DataSource myDataSource() {
		return myDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean
	public AcrossModule myJpaModule() {
		return AcrossHibernateJpaModule.builder().prefix( "my" ).build();
	}

	@Bean
	public EntitiesModule entitiesModule() {
		return new EntitiesModule();
	}

	@Component
	public static class MyEntityInterceptor extends EntityInterceptorAdapter<Persistable<Long>>
	{
		private final Set<Long> created = new HashSet<>();

		@Override
		public boolean handles( Class<?> entityClass ) {
			return true;
		}

		@Override
		public void afterCreate( Persistable<Long> entity ) {
			created.add( entity.getId() );
		}

		public boolean received( Persistable<Long> entity ) {
			return created.contains( entity.getId() );
		}
	}

	public static void main( String[] args ) {
		SpringApplication.run( MultipleDataSourceApplication.class );
	}
}
