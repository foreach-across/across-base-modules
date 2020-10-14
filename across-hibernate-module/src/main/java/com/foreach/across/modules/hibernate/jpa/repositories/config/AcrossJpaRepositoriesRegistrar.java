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
package com.foreach.across.modules.hibernate.jpa.repositories.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.*;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Locale;

import static com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration.DEFAULT_ACROSS_BOOTSTRAP_MODE;

/**
 * {@link ImportBeanDefinitionRegistrar} to enable {@link EnableAcrossJpaRepositories} annotation.
 *
 * @author Arne Vandamme
 */
class AcrossJpaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport
{
	private BootstrapMode bootstrapMode = null;
	private ResourceLoader resourceLoader = null;
	private Environment environment = null;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getAnnotation()
	 */
	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableAcrossJpaRepositories.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getExtension()
	 */
	@Override
	protected RepositoryConfigurationExtension getExtension() {
		return new AcrossJpaRepositoryConfigExtension();
	}

	protected BootstrapMode getBootstrapMode() {
		return ( this.bootstrapMode == null ) ? DEFAULT_ACROSS_BOOTSTRAP_MODE : this.bootstrapMode;
	}

	@Override
	public void setEnvironment( Environment environment ) {
		super.setEnvironment( environment );
		this.environment = environment;
		configureBootstrapMode( environment );
	}

	@Override
	public void setResourceLoader( ResourceLoader resourceLoader ) {
		super.setResourceLoader( resourceLoader );
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void registerBeanDefinitions( AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator ) {
		AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource( metadata,
		                                                                                                           getAnnotation(),
		                                                                                                           resourceLoader,
		                                                                                                           environment, registry,
		                                                                                                           generator )
		{
			@Override
			public BootstrapMode getBootstrapMode() {
				return AcrossJpaRepositoriesRegistrar.this.getBootstrapMode();
			}
		};

		RepositoryConfigurationExtension extension = getExtension();
		RepositoryConfigurationUtils.exposeRegistration( extension, registry, configurationSource );

		AcrossRepositoryConfigurationDelegate delegate = new AcrossRepositoryConfigurationDelegate(
				configurationSource, this.resourceLoader, this.environment );

		delegate.registerRepositoriesIn( registry, getExtension() );
	}

	private void configureBootstrapMode( Environment environment ) {
		String property = environment.getProperty( "spring.data.jpa.repositories.bootstrap-mode" );
		if ( StringUtils.hasText( property ) ) {
			this.bootstrapMode = BootstrapMode.valueOf( property.toUpperCase( Locale.ENGLISH ) );
		}
	}

}
