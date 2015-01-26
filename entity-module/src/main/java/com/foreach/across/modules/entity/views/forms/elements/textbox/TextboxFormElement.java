package com.foreach.across.modules.entity.views.forms.elements.textbox;

import com.foreach.across.modules.entity.views.forms.elements.CommonFormElements;
import com.foreach.across.modules.entity.views.forms.elements.FormElementSupport;

/**
 * Represents a HTML "text" input type.
 */
public class TextboxFormElement extends FormElementSupport
{
	public static final String TYPE = CommonFormElements.TEXTBOX;

	private Integer maxLength;

	public TextboxFormElement() {
		super( TYPE );
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength( Integer maxLength ) {
		this.maxLength = maxLength;
	}
}
