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
package com.foreach.across.modules.entity.views.elements.button;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.ViewElementSupport;

/**
 * @author Arne Vandamme
 */
public class ButtonViewElement extends ViewElementSupport
{
	public static final String TYPE = CommonViewElements.BUTTON;

	private String link;

	public ButtonViewElement() {
		setElementType( TYPE );
	}

	@Override
	public void setElementType( String elementType ) {
		super.setElementType( elementType );
	}

	public String getLink() {
		return link;
	}

	public void setLink( String link ) {
		this.link = link;
	}

	public boolean hasLink() {
		return link != null;
	}
}
