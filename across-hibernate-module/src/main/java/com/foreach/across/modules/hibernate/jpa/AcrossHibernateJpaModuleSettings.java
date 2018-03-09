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

import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SuppressWarnings("all")
@ConfigurationProperties("acrossHibernate")
@Data
@EqualsAndHashCode(callSuper = true)
public class AcrossHibernateJpaModuleSettings extends AcrossHibernateModuleSettings
{
	public static final String PERSISTENCE_UNIT_NAME = "acrossHibernate.persistenceUnitName";
	public static final String PRIMARY = "acrossHibernate.primary";

	/**
	 * Name of the persistence unit that is being managed by this module. Defaults to the module name.
	 */
	private String persistenceUnitName;

	/**
	 * Should this module register its {@link org.springframework.transaction.PlatformTransactionManager},
	 * {@link org.springframework.transaction.support.TransactionTemplate} and
	 * {@link com.foreach.across.modules.hibernate.services.HibernateSessionHolder} as primary when exposing.
	 * <p/>
	 * If set to {@code true} this will also register the default aliases for the
	 * {@link org.springframework.transaction.PlatformTransactionManager} and
	 * {@link org.springframework.transaction.support.TransactionTemplate}.
	 * <p/>
	 * If not set explicitly, this will be {@code true} by default for the standard {@code AcrossHibernateJpaModule},
	 * with the module name {@code AcrossHibernateJpaModule}.
	 */
	private Boolean primary;
}
