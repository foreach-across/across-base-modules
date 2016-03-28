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

package com.foreach.across.modules.hibernate.config.dynamic;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactoryImpl;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

/**
 * Configures a UnitOfWorkFactory for the current SessionFactory.
 */
public class UnitOfWorkConfiguration
{
	@Bean
	@Exposed
	public UnitOfWorkFactory unitOfWork( SessionFactory sessionFactory ) {
		return new UnitOfWorkFactoryImpl( Collections.singleton( sessionFactory ) );
	}
}
