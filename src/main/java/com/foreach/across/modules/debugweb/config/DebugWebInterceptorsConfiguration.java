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
package com.foreach.across.modules.debugweb.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.debugweb.config.support.DebugWebConfigurerAdapter;
import com.foreach.across.modules.debugweb.mvc.DebugMenu;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.mvc.InterceptorRegistry;
import com.foreach.across.modules.web.mvc.WebAppPathResolverExposingInterceptor;
import com.foreach.across.modules.web.resource.*;
import com.foreach.across.modules.web.template.LayoutTemplateProcessorAdapterBean;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class DebugWebInterceptorsConfiguration extends DebugWebConfigurerAdapter
{
	private static final Logger LOG = LoggerFactory.getLogger( DebugWebModule.class );

	@Autowired(required = false)
	private WebResourceTranslator viewsWebResourceTranslator;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private DebugWebModule debugWebModule;

	@Override
	public void addInterceptors( InterceptorRegistry interceptorRegistry ) {
		interceptorRegistry.addInterceptor( new WebAppPathResolverExposingInterceptor( debugWeb() ) );
		interceptorRegistry.addInterceptor( debugWebResourceRegistryInterceptor() );
		interceptorRegistry.addInterceptor( debugWebTemplateInterceptor() );
	}

	@Bean
	@Exposed
	public DebugWeb debugWeb() {
		return new DebugWeb( debugWebModule.getRootPath() );
	}

	@Bean
	public WebTemplateInterceptor debugWebTemplateInterceptor() {
		return new WebTemplateInterceptor( debugWebTemplateRegistry() );
	}

	@Bean
	@Exposed
	public WebTemplateRegistry debugWebTemplateRegistry() {
		WebTemplateRegistry webTemplateRegistry = new WebTemplateRegistry();

		webTemplateRegistry.register( DebugWeb.LAYOUT_TEMPLATE, debugWebLayoutTemplateProcessor() );
		webTemplateRegistry.setDefaultTemplateName( DebugWeb.LAYOUT_TEMPLATE );

		return webTemplateRegistry;
	}

	@Bean
	public LayoutTemplateProcessorAdapterBean debugWebLayoutTemplateProcessor() {
		return new LayoutTemplateProcessorAdapterBean( DebugWeb.LAYOUT_TEMPLATE )
		{
			@Override
			protected void registerWebResources( WebResourceRegistry registry ) {
				registry.addWithKey( WebResource.CSS, DebugWeb.MODULE, DebugWeb.CSS_MAIN, WebResource.VIEWS );
				registry.addWithKey( WebResource.JAVASCRIPT, "jquery",
				                     "//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js",
				                     WebResource.EXTERNAL );
				registry.addWithKey( WebResource.CSS, "bootstrap",
				                     "//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css",
				                     WebResource.EXTERNAL );
				registry.addWithKey( WebResource.JAVASCRIPT_PAGE_END, "bootstrap-js",
				                     "//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js",
				                     WebResource.EXTERNAL );
			}

			@Override
			protected void buildMenus( MenuFactory menuFactory ) {
				menuFactory.buildMenu( DebugMenu.NAME, DebugMenu.class );
			}
		};
	}

	@Bean
	@Exposed
	public WebResourceRegistryInterceptor debugWebResourceRegistryInterceptor() {
		WebResourceRegistryInterceptor interceptor =
				new WebResourceRegistryInterceptor( debugWebResourcePackageManager() );

		if ( viewsWebResourceTranslator != null ) {
			interceptor.addWebResourceTranslator( viewsWebResourceTranslator );
		}
		else {
			LOG.warn( "No default viewsWebResourceTranslator configured - manual translators will be required." );
		}

		return interceptor;
	}

	@Bean
	@Exposed
	public WebResourcePackageManager debugWebResourcePackageManager() {
		return new WebResourcePackageManager();
	}
}
