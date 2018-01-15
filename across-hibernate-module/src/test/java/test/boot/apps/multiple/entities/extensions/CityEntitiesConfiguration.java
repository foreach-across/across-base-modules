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

package test.boot.apps.multiple.entities.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import test.boot.apps.multiple.entities.city.City;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ModuleConfiguration(AcrossHibernateJpaModule.NAME)
@EntityScan(basePackageClasses = City.class)
public class CityEntitiesConfiguration
{
}
