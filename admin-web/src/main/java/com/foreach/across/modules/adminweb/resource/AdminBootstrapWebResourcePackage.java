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
package com.foreach.across.modules.adminweb.resource;

import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;

import java.util.Arrays;

/**
 * Boostrap css, requires jquery as well.
 */
public class AdminBootstrapWebResourcePackage extends SimpleWebResourcePackage
{
	public static final String NAME = "bootstrap-adminweb";

	public AdminBootstrapWebResourcePackage( boolean minified ) {
		this( minified, "3.1.1" );
	}

	public AdminBootstrapWebResourcePackage( boolean minified, String version ) {
		if ( minified ) {
			setWebResources( Arrays.asList( new WebResource( WebResource.CSS, NAME,
			                                                 "/css/adminweb/styles-light-theme.css",
			                                                 WebResource.VIEWS ),
//			                                new WebResource( WebResource.CSS, NAME + "-theme",
//			                                                 "//netdna.bootstrapcdn.com/bootstrap/" + version + "/css/bootstrap-theme.min.css",
//			                                                 WebResource.EXTERNAL ),
                                            new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME,
                                                             "//netdna.bootstrapcdn.com/bootstrap/" + version + "/js/bootstrap.min.js",
                                                             WebResource.EXTERNAL ),
                                            new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-main",
                                                             "/js/adminweb/main.js",
                                                             WebResource.VIEWS ),
                                            new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-ie10-viewport",
                                                             "/js/adminweb/ie10-viewport-bug-workaround.js",
                                                             WebResource.VIEWS )
			                 )
			);
		}
		else {
			setWebResources( Arrays.asList( new WebResource( WebResource.CSS, NAME,
			                                                 "/css/adminweb/styles-light-theme.css",
			                                                 WebResource.VIEWS ),
//			                                new WebResource( WebResource.CSS, NAME + "-theme",
//			                                                 "//netdna.bootstrapcdn.com/bootstrap/" + version + "/css/bootstrap-theme.css",
//			                                                 WebResource.EXTERNAL ),
                                            new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME,
                                                             "//netdna.bootstrapcdn.com/bootstrap/" + version + "/js/bootstrap.js",
                                                             WebResource.EXTERNAL ),
                                            new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-main",
                                                             "/js/adminweb/main.js",
                                                             WebResource.VIEWS ),
                                            new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-ie10-viewport",
                                                             "/js/adminweb/ie10-viewport-bug-workaround.js",
                                                             WebResource.VIEWS )
			                 )
			);
		}
	}

	@Override
	public void install( WebResourceRegistry registry ) {
		registry.addPackage( JQueryWebResourcePackage.NAME );

		super.install( registry );
	}
}
