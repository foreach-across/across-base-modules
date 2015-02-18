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
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.views.elements.ViewElements;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;

/**
 * Convenient helper combining the different view processors and providing most common extension points.
 *
 * @author Andy Somers, Arne Vandamme
 */
public class ViewProcessorAdapter<T extends ViewCreationContext, U extends EntityView>
		implements ViewPreProcessor<T, U>, ViewPostProcessor<T, U>, ViewDataBinderProcessor<T>, ViewModelAndCommandProcessor<T>
{
	@Override
	public void prepareModelAndCommand( String viewName,
	                                    T creationContext,
	                                    EntityViewCommand command,
	                                    ModelMap model ) {
	}

	@Override
	public void prepareDataBinder( String viewName,
	                               T creationContext,
	                               EntityViewCommand command,
	                               DataBinder dataBinder ) {
	}

	@Override
	public final void preProcess( T creationContext, U view ) {
		applyCustomPreProcessing( creationContext, view );
	}

	protected void applyCustomPreProcessing( T creationContext, U view ) {
	}

	@Override
	public final void postProcess( T creationContext, U view ) {
		applyCustomPostProcessing( creationContext, view );

		modifyViewElements( view.getEntityProperties() );
	}

	protected void applyCustomPostProcessing( T creationContext, U view ) {

	}

	protected void modifyViewElements( ViewElements elements ) {

	}
}
