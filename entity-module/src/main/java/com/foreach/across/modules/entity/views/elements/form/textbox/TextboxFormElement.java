package com.foreach.across.modules.entity.views.elements.form.textbox;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementSupport;

/**
 * Represents a HTML "text" input type as well as "textarea" types.
 */
public class TextboxFormElement extends FormElementSupport
{
	public static final String TYPE = CommonViewElements.TEXTBOX;

	private Integer maxLength;
	private boolean url= false;
	private boolean multiLine = true;

	public TextboxFormElement() {
		super( TYPE );
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
		return multiLine;
	}

	public void setMultiLine( boolean multiLine ) {
		this.multiLine = multiLine;
	}
}
