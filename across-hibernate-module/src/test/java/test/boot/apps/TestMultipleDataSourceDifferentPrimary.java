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

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import test.boot.apps.multiple.MultipleDataSourceApplication;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		classes = MultipleDataSourceApplication.class,
		properties = {
				"app.datasource.foo.url=jdbc:hsqldb:mem:foo-db",
				"app.datasource.bar.url=jdbc:hsqldb:mem:bar-db",
				"app.datasource.my.url=jdbc:hsqldb:mem:my-db",
				"spring.jpa.show-sql=false",
				"spring.transaction.default-timeout=25",
				"acrossHibernate.primary=false",
				"acrossHibernate.hibernate.ddl-auto=create-drop",
				"customConnector.primary=true",
				"customConnector.generate-ddl=true",
				"customConnector.data-source=barDataSource",
				"my.jpa.generate-ddl=true"
		}
)
public class TestMultipleDataSourceDifferentPrimary
{
	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	@Test
	public void primaryTransactionManagerIsFromCustomConnectorModule() {
		assertThat( beanRegistry.getBeansOfType( PlatformTransactionManager.class ) ).hasSize( 3 );
		assertThat( beanRegistry.getBeansOfType( TransactionTemplate.class ) ).hasSize( 3 );

		assertThat( beanRegistry.getBeanOfType( PlatformTransactionManager.class ) )
				.isEqualTo( beanRegistry.getBean( "transactionManager" ) )
				.isEqualTo( beanRegistry.getBean( "customJpaTransactionManager" ) );

		assertThat( beanRegistry.getBeanOfType( TransactionTemplate.class ) )
				.isEqualTo( beanRegistry.getBean( "transactionTemplate" ) )
				.isEqualTo( beanRegistry.getBean( "customJpaTransactionTemplate" ) );
	}
}
