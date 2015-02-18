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

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;

/**
 * @author Arne Vandamme
 */
public class EntityViewRequest extends EntityViewCommand
{
	private String viewName;
	private EntityViewFactory viewFactory;
	private WebViewCreationContext creationContext;
	private String partialFragment;

	public EntityViewRequest( String viewName,
	                          EntityViewFactory viewFactory,
	                          WebViewCreationContext creationContext ) {
		this.viewName = viewName;
		this.viewFactory = viewFactory;
		this.creationContext = creationContext;
	}

	public String getViewName() {
		return viewName;
	}

	public EntityViewFactory getViewFactory() {
		return viewFactory;
	}

	public WebViewCreationContext getCreationContext() {
		return creationContext;
	}

	@SuppressWarnings("unchecked")
	public EntityView createView( Model model ) {
		EntityView view = viewFactory.create( viewName, creationContext, model );

		if ( partialFragment != null ) {
			view.setViewName( StringUtils.join( new Object[] { view.getViewName(), partialFragment }, " :: " ) );
		}

		return view;
	}

	public void initDataBinder( WebDataBinder dataBinder ) {
		viewFactory.prepareDataBinder( viewName, creationContext, this, dataBinder );
	}

	@SuppressWarnings("unchecked")
	public void prepareModelAndCommand( ExtendedModelMap model ) {
		viewFactory.prepareModelAndCommand( viewName, creationContext, this, model );
	}

	public void setPartialFragment( String partialFragment ) {
		this.partialFragment = partialFragment;
	}
}
