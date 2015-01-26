package com.foreach.across.modules.entity.views.forms.elements.hidden;

import com.foreach.across.modules.entity.views.forms.elements.CommonFormElements;
import com.foreach.across.modules.entity.views.forms.elements.FormElementSupport;

/**
 * Represents a HTML "hidden" input type.
 */
public class HiddenFormElement extends FormElementSupport
{
	public static final String TYPE = CommonFormElements.HIDDEN;

	public HiddenFormElement() {
		super( TYPE );
	}
}
