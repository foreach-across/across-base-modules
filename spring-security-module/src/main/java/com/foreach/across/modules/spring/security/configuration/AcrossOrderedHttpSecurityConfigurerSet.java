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

import com.foreach.across.core.context.AcrossListableBeanFactory;
import com.foreach.across.core.context.AcrossOrderSpecifierComparator;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.support.AcrossOrderSpecifier;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author Davy
 * @since 5.5.0
 */
class AcrossOrderedHttpSecurityConfigurerSet extends LinkedHashSet<HttpSecurityBuilder<? extends SecurityBuilder<? extends SecurityFilterChain>>>
{
	private final AcrossListableBeanFactory beanFactory;

	AcrossOrderedHttpSecurityConfigurerSet( AcrossModuleInfo currentModule ) {
		beanFactory = (AcrossListableBeanFactory) currentModule.getApplicationContext().getAutowireCapableBeanFactory();

		AcrossOrderSpecifierComparator comparator = new AcrossOrderSpecifierComparator();
		Set<Object> configurers = new LinkedHashSet<>();

		addWebSecurityConfigurers( WebSecurityConfigurer.class, configurers, comparator );
		addWebSecurityConfigurers( AcrossWebSecurityConfigurer.class, configurers, comparator );

		List<Object> sortedList = new ArrayList<>( configurers );
		sortedList.sort( comparator );

		for ( int i = 0; i < sortedList.size(); i++ ) {
			add( createWrapperOrProxy( sortedList.get( i ), i ) );
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HttpSecurityBuilder<? extends HttpSecurityBuilder<? extends SecurityFilterChain>> createWrapperOrProxy( Object original, int order ) {
/*
		if ( original instanceof AcrossWebSecurityConfigurer ) {
			return createWrapper( (AcrossWebSecurityConfigurer) original, order );
		}
		throw new IllegalArgumentException( "Not supported: " + original );
		//return createProxy( (WebSecurityConfigurer) original, order );
*/
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private WebSecurityConfigurerProxy<? extends SecurityBuilder<Filter>> createProxy( HttpSecurityBuilder<? extends SecurityBuilder<? extends SecurityFilterChain>> configurer,
	                                                                                   int order ) {
		InterfaceMaker interfaceMaker = new InterfaceMaker();
		Class<?> dynamicInterface = interfaceMaker.create();

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( WebSecurityConfigurerProxy.class );
		enhancer.setInterfaces( new Class[] { dynamicInterface } );
		enhancer.setCallback( NoOp.INSTANCE );

		return (WebSecurityConfigurerProxy) enhancer.create(
				new Class[] { WebSecurityConfigurer.class, int.class },
				new Object[] { configurer, order }
		);
	}

	/**
	 * Creates a dynamic wrapper for a AcrossWebSecurityConfigurer.  Due to the standard configuration of http
	 * security, the wrapper MUST be a different class each time.  To achieve that, a dynamically created interface
	 * is added to every wrapper.
	 */
	private AcrossWebSecurityConfigurerAdapter createWrapper( AcrossWebSecurityConfigurer configurer, int order ) {
		InterfaceMaker interfaceMaker = new InterfaceMaker();
		Class<?> dynamicInterface = interfaceMaker.create();

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( AcrossWebSecurityConfigurerAdapter.class );
		enhancer.setInterfaces( new Class[] { dynamicInterface } );
		enhancer.setCallback( NoOp.INSTANCE );

		AcrossWebSecurityConfigurerAdapter wrapper = (AcrossWebSecurityConfigurerAdapter) enhancer.create(
				new Class[] { AcrossWebSecurityConfigurer.class, int.class },
				new Object[] { configurer, order }
		);

		beanFactory.autowireBean( wrapper );

		return wrapper;
	}

	private void addWebSecurityConfigurers( Class<?> configurerType, Set<Object> list, AcrossOrderSpecifierComparator comparator ) {
		Map<String, ?> configurers = beanFactory.getBeansOfType( configurerType );

		configurers.forEach( ( beanName, configurer ) -> {
			comparator.register( configurer, beanFactory.retrieveOrderSpecifier( beanName ) );
			list.add( configurer );
		} );

		ListableBeanFactory pbf = (ListableBeanFactory) beanFactory.getParentBeanFactory();

		if ( pbf != null ) {
			BeanFactoryUtils.beansOfTypeIncludingAncestors( pbf, configurerType )
			                .forEach( ( beanName, configurer ) -> {
				                if ( !list.contains( configurer ) ) {
					                comparator.register( configurer, AcrossOrderSpecifier.forSources( Collections.singletonList( configurer ) ).build() );
					                list.add( configurer );
				                }
			                } );
		}
	}

	@SuppressWarnings({ "WeakerAccess" })
	public List<HttpSecurityBuilder<? extends SecurityBuilder<? extends SecurityFilterChain>>> getHttpSecurityBuilders() {
		return new ArrayList<>( this );
	}
}
