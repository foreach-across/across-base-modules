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
package com.foreach.across.modules.entity.views.elements.form.select;

import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderSupport;
import org.springframework.util.Assert;

/**
 * @author Arne Vandamme
 */
public class SelectFormElementBuilder extends FormElementBuilderSupport<SelectFormElement>
{
	private SelectOptionGenerator optionGenerator;

	private String elementType = SelectFormElement.TYPE;

	public SelectFormElementBuilder() {
		super( SelectFormElement.class );
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType( String elementType ) {
		this.elementType = elementType;
	}

	public SelectOptionGenerator getOptionGenerator() {
		return optionGenerator;
	}

	public void setOptionGenerator( SelectOptionGenerator optionGenerator ) {
		Assert.notNull( optionGenerator );
		this.optionGenerator = optionGenerator;
	}

	@Override
	protected void buildCustomProperties( SelectFormElement element ) {
		element.setOptions( optionGenerator.generateOptions( getMessageCodeResolver() ) );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof SelectFormElementBuilder ) ) {
			return false;
		}
		if ( !super.equals( o ) ) {
			return false;
		}

		SelectFormElementBuilder that = (SelectFormElementBuilder) o;

		if ( optionGenerator != null ? !optionGenerator.equals(
				that.optionGenerator ) : that.optionGenerator != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ( optionGenerator != null ? optionGenerator.hashCode() : 0 );
		return result;
	}
}
