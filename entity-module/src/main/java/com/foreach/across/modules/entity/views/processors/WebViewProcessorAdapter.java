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
package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.elements.ViewElements;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;

/**
 * Convenient helper combining the different view processors and providing most common extension points.
 * Helper class is tailored for a prototype style processor: processor will only be applied to a single
 * view and implies the creation context from that knowledge.
 *
 * @author Andy Somers, Arne Vandamme
 */
public class WebViewProcessorAdapter<U extends EntityView>
		implements ViewPreProcessor<WebViewCreationContext, U>, ViewPostProcessor<WebViewCreationContext, U>, ViewDataBinderProcessor<WebViewCreationContext>, ViewModelAndCommandProcessor<WebViewCreationContext>
{
	@Override
	public final void prepareModelAndCommand( String viewName,
	                                          WebViewCreationContext creationContext,
	                                          EntityViewCommand command,
	                                          ModelMap model ) {
		registerCommandExtensions( command );
		customizeModelAndCommand( viewName, creationContext, command, model );
	}

	protected void customizeModelAndCommand( String viewName,
	                                         WebViewCreationContext creationContext,
	                                         EntityViewCommand command,
	                                         ModelMap model ) {
	}

	protected void registerCommandExtensions( EntityViewCommand command ) {
	}

	@Override
	public final void prepareDataBinder( String viewName,
	                                     WebViewCreationContext creationContext,
	                                     EntityViewCommand command,
	                                     DataBinder dataBinder ) {
		prepareDataBinder( (WebDataBinder) dataBinder, command );
		customizeDataBinder( viewName, creationContext, command, (WebDataBinder) dataBinder );
	}

	protected void customizeDataBinder( String viewName,
	                                    WebViewCreationContext creationContext,
	                                    EntityViewCommand command,
	                                    WebDataBinder dataBinder ) {

	}

	protected void prepareDataBinder( WebDataBinder dataBinder, EntityViewCommand command ) {

	}

	@Override
	public final void preProcess( WebViewCreationContext creationContext, U view ) {
		applyCustomPreProcessing( creationContext, view );
	}

	protected void applyCustomPreProcessing( WebViewCreationContext creationContext, U view ) {

	}

	@Override
	public final void postProcess( WebViewCreationContext creationContext, U view ) {
		registerWebResources( creationContext.getWebResourceRegistry() );

		applyCustomPostProcessing( creationContext, view );

		extendViewModel( view );
		modifyViewElements( view.getEntityProperties() );
	}

	protected void applyCustomPostProcessing( WebViewCreationContext creationContext, U view ) {

	}

	protected void extendViewModel( U view ) {

	}

	protected void modifyViewElements( ViewElements elements ) {

	}

	protected void registerWebResources( WebResourceRegistry resourceRegistry ) {

	}
}
