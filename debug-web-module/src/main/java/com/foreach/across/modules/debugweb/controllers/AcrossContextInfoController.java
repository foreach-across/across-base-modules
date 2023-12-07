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
package com.foreach.across.modules.debugweb.controllers;

import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.context.ExposedBeanDefinition;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.config.PropertyMaskingProperties;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.debugweb.util.ContextDebugInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.*;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@DebugWebController
class AcrossContextInfoController
{
	private final AcrossContextInfo acrossContext;
	private final PropertyMaskingProperties propertyMaskingProperties;

	@EventListener
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .group( "/across", "Across" ).and()
		     .item( "/across/browser", "Context browser", "/across/browser/info/-1" ).order(
				Ordered.HIGHEST_PRECEDENCE );
	}

	@ModelAttribute("contexts")
	public List<ContextDebugInfo> contextDebugInfoList() {
		return ContextDebugInfo.create( acrossContext );
	}

	@RequestMapping("/across/browser/info/{index}")
	public String showContextInfo( @ModelAttribute("contexts") List<ContextDebugInfo> contexts,
	                               @PathVariable("index") int index,
	                               Model model ) {
		ContextDebugInfo selected = selectContext( contexts, index );

		model.addAttribute( "selectedContextIndex", contexts.indexOf( selected ) );
		model.addAttribute( "selectedContext", selected );
		model.addAttribute( "section", "info" );
		model.addAttribute( "sectionTemplate", DebugWeb.VIEW_BROWSER_INFO );

		return DebugWeb.LAYOUT_BROWSER;
	}

	@RequestMapping("/across/browser/beans/{index}")
	public String showContextBeans( @ModelAttribute("contexts") List<ContextDebugInfo> contexts,
	                                @PathVariable("index") int index,
	                                Model model ) {
		ContextDebugInfo selected = selectContext( contexts, index );

		Collection<BeanInfo> beans = buildBeanSet( selected );

		int exposedCount = 0;
		for ( BeanInfo beanInfo : beans ) {
			if ( beanInfo.isExposed() ) {
				exposedCount++;
			}
		}

		model.addAttribute( "contextBeans", beans );
		model.addAttribute( "totalBeanCount", beans.size() );
		model.addAttribute( "exposedBeanCount", exposedCount );

		model.addAttribute( "selectedContextIndex", contexts.indexOf( selected ) );
		model.addAttribute( "selectedContext", selected );
		model.addAttribute( "section", "beans" );
		model.addAttribute( "sectionTemplate", DebugWeb.VIEW_BROWSER_BEANS );

		return DebugWeb.LAYOUT_BROWSER;
	}

	@RequestMapping("/across/browser/properties/{index}")
	public String showContextProperties( @ModelAttribute("contexts") List<ContextDebugInfo> contexts,
	                                     @PathVariable("index") int index,
	                                     Model model ) {
		ContextDebugInfo selected = selectContext( contexts, index );

		model.addAttribute( "propertySources", getPropertySources( selected.getEnvironment() ) );

		model.addAttribute( "selectedContextIndex", contexts.indexOf( selected ) );
		model.addAttribute( "selectedContext", selected );
		model.addAttribute( "section", "properties" );
		model.addAttribute( "sectionTemplate", DebugWeb.VIEW_BROWSER_PROPERTIES );

		return DebugWeb.LAYOUT_BROWSER;
	}

	private ContextDebugInfo selectContext( List<ContextDebugInfo> contexts, int index ) {
		if ( index < 0 ) {
			ApplicationContext applicationContext = AcrossContextUtils.getApplicationContext( acrossContext );
			for ( ContextDebugInfo context : contexts ) {
				if ( context.getApplicationContext() == applicationContext ) {
					return context;
				}
			}
		}

		return contexts.get( index );
	}

	static class PropertyValueMasker
	{
		public static final String MASK = "******";

		private final String[] masks, maskedProperties;

		public PropertyValueMasker( String[] masks, String[] maskedProperties ) {
			this.masks = masks;
			this.maskedProperties = maskedProperties;
		}

		public Object maskIfNecessary( String name, Object value ) {
			if ( name != null ) {
				if ( ArrayUtils.contains( maskedProperties, name ) ) {
					return MASK;
				}

				for ( String mask : masks ) {
					if ( name.matches( mask ) ) {
						return MASK;
					}
				}
			}

			return value;
		}
	}

	static class PropertySourceInfo
	{
		private PropertySource propertySource;
		private Collection<PropertyInfo> properties;

		PropertySourceInfo( PropertySource propertySource ) {
			this.propertySource = propertySource;
		}

		public String getName() {
			return propertySource.getName();
		}

		public String getPropertySourceType() {
			return propertySource.getClass().getCanonicalName();
		}

		public Collection<PropertyInfo> getProperties() {
			return properties;
		}

		public void setProperties( Collection<PropertyInfo> properties ) {
			this.properties = properties;
		}

		public boolean isSystemSource() {
			return org.apache.commons.lang3.StringUtils.equalsIgnoreCase( "systemproperties",
			                                                              propertySource.getName() );
		}

		public boolean isEnvironmentSource() {
			return org.apache.commons.lang3.StringUtils.equalsIgnoreCase( "systemenvironment",
			                                                              propertySource.getName() );
		}

		public boolean isEnumerable() {
			return propertySource instanceof EnumerablePropertySource;
		}

		public boolean isEmpty() {
			return properties == null || properties.isEmpty();
		}
	}

	static class PropertyInfo implements Comparable<PropertyInfo>
	{
		private String name;
		private Environment environment;
		private PropertySource propertySource;
		private PropertyValueMasker valueMasker;

		PropertyInfo( String name,
		              Environment environment,
		              PropertySource propertySource,
		              PropertyValueMasker valueMasker ) {
			this.name = name;
			this.environment = environment;
			this.propertySource = propertySource;
			this.valueMasker = valueMasker;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return valueMasker.maskIfNecessary( name, propertySource.getProperty( name ) );
		}

		public Object getEnvironmentValue() {
			try {
				return valueMasker.maskIfNecessary( name, environment.getProperty( name, Object.class ) );
			}
			catch ( IllegalArgumentException ex ) {
				if ( ex.getMessage().contains( "Could not resolve placeholder" ) ) {
					return getValue();
				}
				else {
					throw ex;
				}
			}

		}

		public boolean isActualValue() {
			return Objects.equals( getValue(), getEnvironmentValue() );
		}

		public boolean isSystemProperty() {
			return org.apache.commons.lang3.StringUtils.equalsIgnoreCase( "systemproperties",
			                                                              propertySource.getName() );
		}

		public boolean isEnvironmentProperty() {
			return org.apache.commons.lang3.StringUtils.equalsIgnoreCase( "systemenvironment",
			                                                              propertySource.getName() );
		}

		@Override
		public int compareTo( PropertyInfo o ) {
			return getName().compareTo( o.getName() );
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			PropertyInfo that = (PropertyInfo) o;

			if ( name != null ? !name.equals( that.name ) : that.name != null ) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return name != null ? name.hashCode() : 0;
		}
	}

	private Collection<PropertySourceInfo> getPropertySources( Environment environment ) {
		LinkedList<PropertySourceInfo> sources = new LinkedList<>();

		if ( environment instanceof ConfigurableEnvironment ) {
			PropertyValueMasker valueMasker = new PropertyValueMasker(
					propertyMaskingProperties.getMasks(), propertyMaskingProperties.getMaskedProperties()
			);
			MutablePropertySources propertySources = ( (ConfigurableEnvironment) environment ).getPropertySources();
			for ( PropertySource<?> propertySource : propertySources ) {
				PropertySourceInfo sourceInfo = new PropertySourceInfo( propertySource );

				if ( propertySource instanceof EnumerablePropertySource ) {
					EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource;
					List<PropertyInfo> properties = new ArrayList<>();

					for ( String propertyName : enumerablePropertySource.getPropertyNames() ) {
						properties.add(
								new PropertyInfo( propertyName, environment, enumerablePropertySource, valueMasker )
						);
					}

					Collections.sort( properties );
					sourceInfo.setProperties( properties );
				}

				sources.addFirst( sourceInfo );
			}
		}

		return sources;
	}

	@RequestMapping("/across/browser/handlers/{index}")
	public String showContextHandlers( @ModelAttribute("contexts") List<ContextDebugInfo> contexts,
	                                   @PathVariable("index") int index,
	                                   Model model ) {
		ContextDebugInfo selected = contexts.get( index );

		Collection<BeanInfo> beans = buildBeanSet( selected );

		model.addAttribute( "handlerMethodsByEvent",
		                    beans.stream()
		                         .filter( BeanInfo::isEventHandler )
		                         .map( BeanInfo::getHandlerMethods )
		                         .flatMap( Collection::stream )
		                         .collect( Collectors.groupingBy(
				                         method -> method.getParameterTypes()[0].getName(),
				                         TreeMap::new,
				                         Collectors.mapping( m -> m, Collectors.toList() ) ) )
		);

		model.addAttribute( "selectedContextIndex", contexts.indexOf( selected ) );
		model.addAttribute( "selectedContext", selected );
		model.addAttribute( "section", "handlers" );
		model.addAttribute( "sectionTemplate", DebugWeb.VIEW_BROWSER_HANDLERS );

		return DebugWeb.LAYOUT_BROWSER;
	}

	private Collection<BeanInfo> buildBeanSet( ContextDebugInfo context ) {
		List<BeanInfo> beans = new LinkedList<>();

		if ( context.isEnabled() ) {

			Map<String, ExposedBeanDefinition> exposed = getExposedBeans( context );

			BeanFactory autowireCapableBeanFactory = context.getApplicationContext().getAutowireCapableBeanFactory();

			if ( autowireCapableBeanFactory instanceof ConfigurableListableBeanFactory ) {
				ConfigurableListableBeanFactory beanFactory =
						(ConfigurableListableBeanFactory) autowireCapableBeanFactory;

				String[] definitions = beanFactory.getBeanDefinitionNames();
				Set<String> names = new HashSet<>();
				names.addAll( Arrays.asList( definitions ) );
				names.addAll( Arrays.asList( beanFactory.getSingletonNames() ) );

				for ( String name : names ) {
					BeanInfo info = new BeanInfo();
					info.setName( name );
					info.setExposed( exposed.containsKey( name ) );

					if ( info.isExposed() ) {
						info.setExposedInfo( buildExposedInfo( exposed.get( name ) ) );
					}

					Class beanType = Object.class;
					Class actual;

					if ( ArrayUtils.contains( definitions, name ) ) {
						BeanDefinition definition = beanFactory.getBeanDefinition( name );
						info.setSingleton( definition.isSingleton() );
						info.setScope( definition.getScope() );

						if ( definition instanceof ExposedBeanDefinition ) {
							info.setExposedBean( true );
							info.setExposedBeanInfo( buildExposedBeanInfo( (ExposedBeanDefinition) definition ) );
							info.setScope( "exposed" );
						}

						try {
							if ( beanFactory.isSingleton( name ) ) {
								Object value = beanFactory.getSingleton( name );
								info.setInstance( value );
								actual = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( value ) );

								if ( value != null ) {
									beanType = value.getClass();
								}
							}
							else {
								beanType = Class.forName( definition.getBeanClassName() );
								actual = ClassUtils.getUserClass( beanType );
							}
						}
						catch ( Exception e ) {
							beanType = null;
							actual = null;
						}
					}
					else {
						info.setSingleton( true );
						info.setScope( BeanDefinition.SCOPE_SINGLETON );

						Object value = beanFactory.getSingleton( name );
						info.setInstance( value );
						try {
							actual = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( value ) );
						}
						catch ( Exception e ) {
							beanType = null;
							actual = null;
						}
					}

					if ( actual != null ) {
						info.setBeanType( actual.getName() );
					}
					if ( beanType != actual ) {
						info.setProxiedOrEnhanced( true );
					}

					detectEventHandlers( info );

					beans.add( info );
				}
			}

			Collections.sort( beans );
		}

		return beans;
	}

	private Map<String, ExposedBeanDefinition> getExposedBeans( ContextDebugInfo context ) {
		if ( context.isModule() ) {
			return context.getModuleInfo().getExposedBeanDefinitions();
		}

		if ( context.isAcrossContext() ) {
			return context.getContextInfo().getExposedBeanDefinitions();
		}

		return Collections.emptyMap();
	}

	private void detectEventHandlers( BeanInfo beanInfo ) {
		Object value = beanInfo.getInstance();

		if ( value != null ) {
			Collection<Method> handlerMethods = detectHandlerMethods( value );

			if ( !handlerMethods.isEmpty() ) {
				beanInfo.setEventHandler( true );
				beanInfo.setHandlerMethods( handlerMethods );
			}
		}
	}

	private String buildExposedBeanInfo( ExposedBeanDefinition exposedBeanDefinition ) {
		if ( StringUtils.isBlank( exposedBeanDefinition.getModuleName() ) ) {
			return String.format( "Exposed bean from context %s, original bean: %s",
			                      exposedBeanDefinition.getContextId(),
			                      exposedBeanDefinition.getOriginalBeanName() );
		}

		return String.format( "Exposed bean from module %s in context %s, original bean: %s",
		                      exposedBeanDefinition.getModuleName(),
		                      exposedBeanDefinition.getContextId(),
		                      exposedBeanDefinition.getOriginalBeanName() );
	}

	private String buildExposedInfo( ExposedBeanDefinition exposedBeanDefinition ) {
		return String.format( "Exposed with preferred beanName: %s", exposedBeanDefinition.getPreferredBeanName() );
	}

	public Collection<Method> detectHandlerMethods( Object value ) {
		List<Method> methods = new LinkedList<>();

		if ( value != null ) {
			Method[] declaredMethods = ReflectionUtils.getUniqueDeclaredMethods( value.getClass() );

			for ( Method method : declaredMethods ) {
				if ( AnnotationUtils.findAnnotation( method, EventListener.class ) != null ) {
					methods.add( method );
				}
			}
		}

		return methods;
	}

	@SuppressWarnings("all")
	public static class BeanInfo implements Comparable<BeanInfo>
	{
		private Object instance;
		private boolean exposed, exposedBean, inherited, singleton, proxiedOrEnhanced, eventHandler;
		private String name, scope, beanType, exposedInfo, exposedBeanInfo;
		private Collection<Method> handlerMethods;

		public boolean isEventHandler() {
			return eventHandler;
		}

		public void setEventHandler( boolean eventHandler ) {
			this.eventHandler = eventHandler;
		}

		public Collection<Method> getHandlerMethods() {
			return handlerMethods;
		}

		public void setHandlerMethods( Collection<Method> handlerMethods ) {
			this.handlerMethods = handlerMethods;
		}

		public Object getInstance() {
			return instance;
		}

		public void setInstance( Object instance ) {
			this.instance = instance;
		}

		public void setExposed( boolean exposed ) {
			this.exposed = exposed;
		}

		public void setInherited( boolean inherited ) {
			this.inherited = inherited;
		}

		public void setSingleton( boolean singleton ) {
			this.singleton = singleton;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public void setScope( String scope ) {
			this.scope = scope;
		}

		public boolean isExposed() {
			return exposed;
		}

		public boolean isExposedBean() {
			return exposedBean;
		}

		public void setExposedBean( boolean exposedBean ) {
			this.exposedBean = exposedBean;
		}

		public String getExposedInfo() {
			return exposedInfo;
		}

		public void setExposedInfo( String exposedInfo ) {
			this.exposedInfo = exposedInfo;
		}

		public String getExposedBeanInfo() {
			return exposedBeanInfo;
		}

		public void setExposedBeanInfo( String exposedBeanInfo ) {
			this.exposedBeanInfo = exposedBeanInfo;
		}

		public boolean isSingleton() {
			return singleton;
		}

		public boolean isInherited() {
			return inherited;
		}

		public String getName() {
			return name;
		}

		public String getScope() {
			return scope;
		}

		public String getBeanType() {
			return beanType;
		}

		public void setBeanType( String beanType ) {
			this.beanType = beanType;
		}

		public boolean isProxiedOrEnhanced() {
			return proxiedOrEnhanced;
		}

		public void setProxiedOrEnhanced( boolean proxiedOrEnhanced ) {
			this.proxiedOrEnhanced = proxiedOrEnhanced;
		}

		public int compareTo( BeanInfo o ) {
			return getName().compareTo( o.getName() );
		}

		public boolean isStandardSpring() {
			return StringUtils.startsWithIgnoreCase( getName(), "org.springframework." );
		}
	}
}
