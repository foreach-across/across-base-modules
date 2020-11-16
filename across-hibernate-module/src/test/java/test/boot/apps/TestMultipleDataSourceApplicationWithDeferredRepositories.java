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

package test.boot.apps;

import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.context.AcrossEntity;
import com.foreach.across.core.context.AcrossListableBeanFactory;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import com.foreach.across.test.ExposeForTest;
import org.assertj.db.api.Assertions;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import test.boot.apps.multiple.MultipleDataSourceApplication;
import test.boot.apps.multiple.application.brand.Brand;
import test.boot.apps.multiple.application.brand.BrandRepository;
import test.boot.apps.multiple.application.car.Car;
import test.boot.apps.multiple.application.car.CarRepository;
import test.boot.apps.multiple.application.street.Street;
import test.boot.apps.multiple.application.street.StreetRepository;
import test.boot.apps.multiple.entities.city.City;
import test.boot.apps.multiple.entities.city.CityRepository;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		classes = MultipleDataSourceApplication.class,
		properties = {
				"app.datasource.foo.url=jdbc:hsqldb:mem:foo-db-def",
				"app.datasource.bar.url=jdbc:hsqldb:mem:bar-db-def",
				"app.datasource.my.url=jdbc:hsqldb:mem:my-db-def",
				"spring.jpa.show-sql=false",
				"spring.transaction.default-timeout=25",
				"acrossHibernate.hibernate.ddl-auto=create-drop",
				"spring.data.jpa.repositories.bootstrap-mode=deferred",
				"customConnector.generate-ddl=true",
				"customConnector.data-source=barDataSource",
				"customConnector.show-sql=true",
				"my.jpa.show-sql=true",
				"my.jpa.generate-ddl=true"
		}
)
@ExposeForTest(Repository.class)
public class TestMultipleDataSourceApplicationWithDeferredRepositories
{
	@Autowired
	private BrandRepository brandRepository;

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private StreetRepository streetRepository;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private DataSource acrossDataSource;

	@Autowired
	@Qualifier("barDataSource")
	private DataSource barDataSource;

	@Autowired
	@Qualifier("myDataSource")
	private DataSource myDataSource;

	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	@Autowired
	private MultipleDataSourceApplication.MyEntityInterceptor interceptor;

	@Autowired
	private BeanFactory beanFactory;

	@Test
	public void shouldHaveDefferedInitializerEventListener() {
		assertTrue( beanRegistry.moduleContainsLocalBean( "AcrossHibernateJpaModule",
		                                                  "com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration.DeferredRepositoryInitializer" ) );

		AcrossListableBeanFactory acrossContext = AcrossContextUtils.getBeanFactory(
				beanFactory.getBean( "acrossContext", AcrossEntity.class ) );
		assertTrue( acrossContext.getBeanDefinition( "test.boot.apps.multiple.entities.city.CityRepository" ).isLazyInit() );
		assertTrue( acrossContext.getBeanDefinition( "test.boot.apps.multiple.application.street.StreetRepository" ).isLazyInit() );
		assertTrue( acrossContext.getBeanDefinition( "test.boot.apps.multiple.application.brand.BrandRepository" ).isLazyInit() );

		assertThat( brandRepository ).isInstanceOf( Advised.class );
		TargetSource targetSource = ( (Advised) brandRepository ).getTargetSource();
		assertThat( Repository.class ).isAssignableFrom( targetSource.getTargetClass() );
	}

	@Test
	public void saveAndFetchBrand() {
		Brand brand = new Brand();
		brand.setName( "Heineken" );
		brandRepository.save( brand );

		assertThat( brandRepository.findOneByName( "Heineken" ) ).isEqualTo( brand );
		assertThat( interceptor.received( brand ) ).isTrue();

		Table table = new Table( acrossDataSource, "brand" );
		Assertions.assertThat( table )
		          .row()
		          .value( "id" ).isGreaterThan( 0L )
		          .value( "name" ).isEqualTo( "Heineken" );
	}

	@Test
	public void saveAndFetchCity() {
		City city = new City();
		city.setName( "Antwerp" );
		cityRepository.save( city );

		assertThat( cityRepository.findOneByName( "Antwerp" ) ).isEqualTo( city );
		assertThat( interceptor.received( city ) ).isTrue();

		Table table = new Table( acrossDataSource, "city" );
		Assertions.assertThat( table )
		          .row()
		          .value( "id" ).isGreaterThan( 0L )
		          .value( "name" ).isEqualTo( "Antwerp" );
	}

	@Test
	public void saveAndFetchCar() {
		Car car = new Car();
		car.setName( "Audi" );
		carRepository.save( car );

		assertThat( carRepository.findOneByName( "Audi" ) ).isEqualTo( car );
		assertThat( interceptor.received( car ) ).isTrue();

		Table table = new Table( barDataSource, "car" );
		Assertions.assertThat( table )
		          .row()
		          .value( "id" ).isEqualTo( 1L )
		          .value( "name" ).isEqualTo( "Audi" );
	}

	@Test
	public void saveAndFetchStreet() {
		Street street = new Street();
		street.setName( "My street" );
		streetRepository.save( street );

		assertThat( streetRepository.findOneByName( "My street" ) ).isEqualTo( street );
		assertThat( interceptor.received( street ) ).isTrue();

		Table table = new Table( myDataSource, "street" );
		Assertions.assertThat( table )
		          .row()
		          .value( "id" ).isGreaterThan( 0L )
		          .value( "name" ).isEqualTo( "My street" );
	}

	@Test
	public void primaryTransactionManagerIsFromDefaultModule() {
		assertThat( beanRegistry.getBeansOfType( PlatformTransactionManager.class ) ).hasSize( 3 );
		assertThat( beanRegistry.getBeansOfType( TransactionTemplate.class ) ).hasSize( 3 );

		assertThat( beanRegistry.getBeanOfType( PlatformTransactionManager.class ) )
				.isEqualTo( beanRegistry.getBean( "transactionManager" ) )
				.isEqualTo( beanRegistry.getBean( HibernateJpaConfiguration.TRANSACTION_MANAGER ) );

		assertThat( beanRegistry.getBeanOfType( TransactionTemplate.class ) )
				.isEqualTo( beanRegistry.getBean( "transactionTemplate" ) )
				.isEqualTo( beanRegistry.getBean( HibernateJpaConfiguration.TRANSACTION_TEMPLATE ) );
	}
}
