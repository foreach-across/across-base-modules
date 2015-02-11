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
package com.foreach.across.modules.adminweb.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.config.support.AdminWebConfigurerAdapter;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.resource.AdminBootstrapWebResourcePackage;
import com.foreach.across.modules.adminweb.resource.JQueryWebResourcePackage;
import com.foreach.across.modules.web.context.PrefixingPathRegistry;
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
public class AdminWebInterceptorsConfiguration extends AdminWebConfigurerAdapter
{
	private static final Logger LOG = LoggerFactory.getLogger( AdminWebModule.class );

	@Autowired(required = false)
	private WebResourceTranslator viewsWebResourceTranslator;

	@Autowired
	private AcrossDevelopmentMode developmentMode;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AdminWebModule adminWebModule;

	@Autowired
	private PrefixingPathRegistry prefixingPathRegistry;

	@Override
	public void addInterceptors( InterceptorRegistry interceptorRegistry ) {
		interceptorRegistry.addInterceptor( new WebAppPathResolverExposingInterceptor( adminWeb() ) );
		interceptorRegistry.addInterceptor( adminWebResourceRegistryInterceptor() );
		interceptorRegistry.addInterceptor( adminWebTemplateInterceptor() );
	}

	@Bean(name = AdminWeb.NAME)
	@Exposed
	public AdminWeb adminWeb() {
		AdminWeb adminWeb = new AdminWeb( adminWebModule.getRootPath() );
		prefixingPathRegistry.add( AdminWeb.NAME, adminWeb );

		return adminWeb;
	}

	@Bean
	@Exposed
	public WebResourceRegistryInterceptor adminWebResourceRegistryInterceptor() {
		WebResourceRegistryInterceptor interceptor =
				new WebResourceRegistryInterceptor( adminWebResourcePackageManager() );

		if ( viewsWebResourceTranslator != null ) {
			interceptor.addWebResourceTranslator( viewsWebResourceTranslator );
		}
		else {
			LOG.warn( "No default viewsWebResourceTranslator configured - manual translators will be required." );
		}

		return interceptor;
	}

	@Bean
	public WebTemplateInterceptor adminWebTemplateInterceptor() {
		return new WebTemplateInterceptor( adminWebTemplateRegistry() );
	}

	@Bean
	@Exposed
	public WebTemplateRegistry adminWebTemplateRegistry() {
		WebTemplateRegistry webTemplateRegistry = new WebTemplateRegistry();

		webTemplateRegistry.register( adminLayoutTemplateProcessor() );
		webTemplateRegistry.setDefaultTemplateName( AdminWeb.NAME );

		return webTemplateRegistry;
	}

	@Bean
	@Exposed
	public WebResourcePackageManager adminWebResourcePackageManager() {
		WebResourcePackageManager webResourcePackageManager = new WebResourcePackageManager();
		webResourcePackageManager.register( JQueryWebResourcePackage.NAME,
		                                    new JQueryWebResourcePackage( !developmentMode.isActive() ) );
		webResourcePackageManager.register( AdminBootstrapWebResourcePackage.NAME,
		                                    new AdminBootstrapWebResourcePackage( !developmentMode.isActive() ) );

		return webResourcePackageManager;
	}

	// todo: verify thymeleaf support is enabled
	@Bean
	public LayoutTemplateProcessorAdapterBean adminLayoutTemplateProcessor() {
		return new LayoutTemplateProcessorAdapterBean( AdminWeb.NAME, AdminWeb.LAYOUT_TEMPLATE )
		{
			@Override
			protected void registerWebResources( WebResourceRegistry registry ) {
				registry.addPackage( AdminBootstrapWebResourcePackage.NAME );
				registry.addWithKey( WebResource.CSS, AdminWeb.MODULE, AdminWeb.LAYOUT_TEMPLATE_CSS,
				                     WebResource.VIEWS );
			}

			@Override
			protected void buildMenus( MenuFactory menuFactory ) {
				menuFactory.buildMenu( AdminMenu.NAME, AdminMenu.class );
			}
		};
	}
}
