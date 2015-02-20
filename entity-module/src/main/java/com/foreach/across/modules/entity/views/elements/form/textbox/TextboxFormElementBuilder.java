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
package com.foreach.across.modules.entity.views.elements.form.textbox;

import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderSupport;

/**
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilder extends FormElementBuilderSupport<TextboxFormElement>
{
	private Integer maxLength;
	private boolean url = false;
	private boolean multiLine = true;

	public TextboxFormElementBuilder() {
		super( TextboxFormElement.class );
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength( Integer maxLength ) {
		this.maxLength = maxLength;
	}

	public boolean isUrl() {
		return url;
	}

	public void setUrl( boolean url ) {
		this.url = url;
	}

	public boolean isMultiLine() {
		return multiLine && !url;
	}

	public void setMultiLine( boolean multiLine ) {
		this.multiLine = multiLine;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		if ( !super.equals( o ) ) {
			return false;
		}

		TextboxFormElementBuilder that = (TextboxFormElementBuilder) o;

		if ( multiLine != that.multiLine ) {
			return false;
		}
		if ( maxLength != null ? !maxLength.equals( that.maxLength ) : that.maxLength != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ( maxLength != null ? maxLength.hashCode() : 0 );
		result = 31 * result + ( multiLine ? 1 : 0 );
		return result;
	}
}
