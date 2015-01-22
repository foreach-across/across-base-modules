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
package com.foreach.across.modules.properties.config;

import com.foreach.across.core.AcrossException;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.properties.PropertiesModuleSettings;
import com.foreach.common.spring.convert.HierarchicalConversionService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

/**
 * The PropertiesModule requires a ConversionService to be present.
 * If no ConversionService is detected or configured as property, one will be created and exposed.
 *
 * @author Arne Vandamme
 */
@Configuration
public class ConversionServiceConfiguration
{
	public static final String CONVERSION_SERVICE_BEAN = "propertiesConversionService";

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossModuleInfo currentModuleInfo;

	@Autowired
	@Module(PropertiesModule.NAME)
	private PropertiesModuleSettings settings;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean(name = CONVERSION_SERVICE_BEAN)
	public ConversionService propertiesConversionService() {
		ConversionService conversionServiceToUse = settings.getProperty(
				PropertiesModuleSettings.CONVERSION_SERVICE,
				ConversionService.class
		);

		if ( conversionServiceToUse == null ) {
			String beanName = settings.getProperty( PropertiesModuleSettings.CONVERSION_SERVICE_BEAN );

			if ( beanName != null ) {
				try {
					conversionServiceToUse = (ConversionService) applicationContext.getBean( beanName );
				}
				catch ( NoSuchBeanDefinitionException nsbde ) {
					throw new AcrossException(
							"A ConversionService bean name was specified but there was a problem wiring it", nsbde );
				}
			}
			else {
				HierarchicalConversionService conversionService =
						HierarchicalConversionService.defaultConversionService( getConversionServiceBeanFromParent() );

				if ( conversionService.getParent() == null ) {
					// We created default conversion service, expose it
					currentModuleInfo.getBootstrapConfiguration()
					                 .addExposeFilter( new ClassBeanFilter( ConversionService.class ) );
				}

				conversionServiceToUse = conversionService;
			}
		}

		return conversionServiceToUse;
	}

	private ConversionService getConversionServiceBeanFromParent() {
		try {
			return applicationContext.getParent().getBean( ConversionService.class );
		}
		catch ( BeansException be ) {
			return null;
		}
	}

}
