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
package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractEntityModuleController implements EntityControllerAttributes
{
	@ModelAttribute
	public void init( WebResourceRegistry registry ) {
		registry.addWithKey( WebResource.CSS, EntityModule.NAME, "/css/entity/entity-module.css", WebResource.VIEWS );
		registry.addWithKey( WebResource.JAVASCRIPT_PAGE_END, EntityModule.NAME,
		                     "/js/entity/entity-module.js", WebResource.VIEWS );
		registry.addWithKey( WebResource.CSS, "jqueryui-css",
		                     "//ajax.googleapis.com/ajax/libs/jqueryui/1.11.0/themes/smoothness/jquery-ui.css",
		                     WebResource.EXTERNAL );
		registry.addWithKey( WebResource.JAVASCRIPT_PAGE_END, "jqueryui",
		                     "//ajax.googleapis.com/ajax/libs/jqueryui/1.11.0/jquery-ui.min.js",
		                     WebResource.EXTERNAL );
        registry.addWithKey( WebResource.JAVASCRIPT_PAGE_END, EntityModule.NAME + "-form-elements",
                             "/js/entity/form-elements.js", WebResource.VIEWS );
	}
}
