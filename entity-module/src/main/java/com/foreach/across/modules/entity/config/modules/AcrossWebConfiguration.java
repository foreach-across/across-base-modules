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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
	@EntityValidator
	@SuppressWarnings("unused")
	private Validator entityValidator;

	@Autowired
	private EntityModuleSettings settings;

	@Autowired
	private SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;

	@PostConstruct
	public void removeFallbackPageableAndSetFallbackSort() {
		// Must return an empty Sort instance or a nullpointer will occur in case of paging without Sort
		List<Sort.Order> orders = new ArrayList<>( 1 );
		orders.add( new Sort.Order( "name" ) );
		sortHandlerMethodArgumentResolver.setFallbackSort( new Sort( orders ) );
		orders.clear();

		pageableHandlerMethodArgumentResolver.setFallbackPageable( null );
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
}
