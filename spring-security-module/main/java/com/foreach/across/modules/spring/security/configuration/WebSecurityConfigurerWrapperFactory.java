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
package com.foreach.across.modules.spring.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Creates a {@link com.foreach.across.modules.spring.security.configuration.WebSecurityConfigurerWrapper}
 * for a {@link com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurer} and
 * ensures it is autowired.  Every wrapper is in fact a different, dynamic class.
 *
 * @author Arne Vandamme
 * @since 1.0.3
 */
@Component
public class WebSecurityConfigurerWrapperFactory
{
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Creates a dynamic wrapper for a SpringSecurityWebConfigurer.  Due to the standard configuration of http
	 * security, the wrapper MUST be a different class each time.  To achieve that, a dynamically created interface
	 * is added to every wrapper.
	 */
	public WebSecurityConfigurerWrapper createWrapper( SpringSecurityWebConfigurer configurer, int index ) {
		InterfaceMaker interfaceMaker = new InterfaceMaker();
		Class dynamicInterface = interfaceMaker.create();

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( WebSecurityConfigurerWrapper.class );
		enhancer.setInterfaces( new Class[] { dynamicInterface } );
		enhancer.setCallback( NoOp.INSTANCE );

		WebSecurityConfigurerWrapper wrapper = (WebSecurityConfigurerWrapper) enhancer.create(
				new Class[] { SpringSecurityWebConfigurer.class, int.class },
				new Object[] { configurer, index }
		);

		applicationContext.getAutowireCapableBeanFactory().autowireBean( wrapper );

		return wrapper;
	}
}
