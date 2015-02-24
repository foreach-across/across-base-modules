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
package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.EntityModuleSettings;
import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.controllers.ViewRequestValidator;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.views.thymeleaf.EntityModuleDialect;
import com.foreach.across.modules.entity.web.WebViewCreationContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.util.ClassUtils;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne Vandamme
 */
@AcrossDepends(required = "AcrossWebModule")
@Configuration
@EnableSpringDataWebSupport
public class AcrossWebConfiguration extends WebMvcConfigurerAdapter
{
	private static final String CLASS_THYMELEAF_TEMPLATE_ENGINE = "org.thymeleaf.spring4.SpringTemplateEngine";

	private static final Logger LOG = LoggerFactory.getLogger( AcrossWebConfiguration.class );

	@EntityValidator
	@SuppressWarnings("unused")
	private SmartValidator entityValidator;

	@Autowired
	private EntityModuleSettings settings;

	@Autowired
	private SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;
	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void removeFallbackPageableAndSetFallbackSort() {
		// Must return an empty Sort instance or a nullpointer will occur in case of paging without Sort
		List<Sort.Order> orders = new ArrayList<>( 1 );
		orders.add( new Sort.Order( "name" ) );
		sortHandlerMethodArgumentResolver.setFallbackSort( new Sort( orders ) );
		orders.clear();

		pageableHandlerMethodArgumentResolver.setFallbackPageable( null );

		if ( shouldRegisterThymeleafDialect() ) {
			LOG.debug( "Registering Thymeleaf entity module dialect" );

			Object springTemplateEngine = applicationContext.getBean( "springTemplateEngine" );

			if ( springTemplateEngine instanceof SpringTemplateEngine ) {
				( (SpringTemplateEngine) springTemplateEngine ).addDialect( new EntityModuleDialect() );
				LOG.debug( "Thymeleaf entity module dialect registered successfully." );
			}
			else {
				LOG.warn(
						"Unable to register Thymeleaf entity module dialect as bean springTemplateEngine is not of the right type." );
			}
		}
	}

	@Override
	public void addArgumentResolvers( List<HandlerMethodArgumentResolver> argumentResolvers ) {
		// todo: move to a decent class - ensure only one creation context per request (?)
		argumentResolvers.add( new HandlerMethodArgumentResolver()
		{
			@Override
			public boolean supportsParameter( MethodParameter parameter ) {
				return ViewCreationContext.class.isAssignableFrom( parameter.getParameterType() );
			}

			@Override
			public Object resolveArgument( MethodParameter parameter,
			                               ModelAndViewContainer mavContainer,
			                               NativeWebRequest webRequest,
			                               WebDataBinderFactory binderFactory ) throws Exception {
				WebViewCreationContextImpl ctx = new WebViewCreationContextImpl();
				ctx.setRequest( webRequest );

				return ctx;
			}
		} );
	}

	/**
	 * Register the entity validator as the default web mvc validator if necessary.
	 */
	@Override
	public Validator getValidator() {
		if ( entityValidator != null && settings.shouldRegisterEntityValidatorForMvc() ) {
			return entityValidator;
		}

		return null;
	}

	@Bean
	protected ViewRequestValidator viewRequestValidator() {
		return new ViewRequestValidator();
	}

	private boolean shouldRegisterThymeleafDialect() {
		if ( applicationContext.containsBean( "springTemplateEngine" ) ) {
			ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

			if ( ClassUtils.isPresent( CLASS_THYMELEAF_TEMPLATE_ENGINE, threadClassLoader ) ) {
				return true;
			}

		}

		return false;
	}
}
