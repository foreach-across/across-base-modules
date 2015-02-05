package com.foreach.across.modules.entity.views.elements.form.hidden;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementSupport;

/**
 * Represents a HTML "hidden" input type.
 */
public class HiddenFormElement extends FormElementSupport
{
	public static final String TYPE = CommonViewElements.HIDDEN;

	public HiddenFormElement() {
		super( TYPE );
	}
}
