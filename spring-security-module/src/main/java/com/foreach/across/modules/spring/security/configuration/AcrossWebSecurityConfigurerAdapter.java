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
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Adapts a {@link AcrossWebSecurityConfigurer} into an {@link WebSecurityConfigurerAdapter}.
 * Configurers can be created in their own module, but the a wrapper will be created in
 * SpringSecurityModule which calls the original configurer when applying the security configuration.
 * <p/>
 * This allows module ordering to be applied to simple security configurations.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.core.registry.RefreshableRegistry
 * @since 1.0.3
 */
class AcrossWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter implements Ordered
{
	private final AcrossWebSecurityConfigurer configurer;
	private final int order;

	private ObjectPostProcessor<Object> objectPostProcessor;

	public AcrossWebSecurityConfigurerAdapter( AcrossWebSecurityConfigurer configurer, int order ) {
		super( false );
		this.configurer = configurer;
		this.order = order;
	}

	@Autowired
	@Override
	public void setObjectPostProcessor( ObjectPostProcessor<Object> objectPostProcessor ) {
		super.setObjectPostProcessor( objectPostProcessor );

		this.objectPostProcessor = objectPostProcessor;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	protected void configure( final AuthenticationManagerBuilder auth ) throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( AuthenticationManagerBuilder.class );

		AuthenticationManagerBuilderDelegateInterceptor interceptor =
				new AuthenticationManagerBuilderDelegateInterceptor( auth );

		enhancer.setCallback( interceptor );

		AuthenticationManagerBuilder proxy = (AuthenticationManagerBuilder) enhancer.create(
				new Class[] { ObjectPostProcessor.class }, new Object[] { objectPostProcessor }
		);

		configurer.configure( proxy );

		// If it has not been configured, ignore the local authentication manager
		if ( !interceptor.isCalled() ) {
			super.configure( auth );
		}
	}

	@Override
	public void configure( WebSecurity web ) throws Exception {
		configurer.configure( web );
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception {
		configurer.configure( http );
	}

	private static class AuthenticationManagerBuilderDelegateInterceptor implements InvocationHandler
	{
		private transient final AuthenticationManagerBuilder builder;
		private transient boolean called = false;

		AuthenticationManagerBuilderDelegateInterceptor( AuthenticationManagerBuilder builder ) {
			this.builder = builder;
		}

		public boolean isCalled() {
			return called;
		}

		@Override
		public Object invoke( Object o, Method method, Object[] objects ) throws Throwable {
			// Only object methods
			if ( method.getDeclaringClass() != Object.class ) {
				called = true;
			}
			return method.invoke( builder, objects );
		}
	}

	@Override
	public String toString() {
		return "AcrossWebSecurityConfigurerAdapter{" +
				"configurer=" + ClassUtils.getUserClass( configurer ).getName() +
				", order=" + order +
				'}';
	}
}
