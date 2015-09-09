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
package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.jpa.unitofwork.JpaUnitOfWorkFactoryImpl;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

/**
 * Configures a UnitOfWorkFactory for the current SessionFactory.
 */
@AcrossCondition("settings.createUnitOfWorkFactory")
@Configuration
public class UnitOfWorkConfiguration
{
	@Bean
	@Exposed
	public UnitOfWorkFactory unitOfWork( EntityManagerFactory entityManagerFactory ) {
		return new JpaUnitOfWorkFactoryImpl( Collections.singleton( entityManagerFactory ) );
	}
}
