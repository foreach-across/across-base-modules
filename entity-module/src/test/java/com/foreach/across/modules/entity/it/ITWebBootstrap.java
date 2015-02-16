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
package com.foreach.across.modules.entity.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITWebBootstrap.Config.class)
public class ITWebBootstrap
{
	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	@Test
	public void bootstrappedOk() {
		assertNotNull( beanRegistry.getBeanFromModule( EntityModule.NAME, "entityCreateController" ) );
	}

	@Configuration
	@AcrossTestWebConfiguration
	public static class Config implements AcrossContextConfigurer
	{
		@Bean
		public ConversionService conversionService() {
			return new DefaultFormattingConversionService( true );
		}

		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new EntityModule() );

			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.setExposeFilter( new ClassBeanFilter( ClientRepository.class ) );
			context.addModule( springDataJpaModule );

			context.addModule( new AdminWebModule() );
			context.addModule( new SpringSecurityModule() );
		}
	}
}
