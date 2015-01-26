package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.entity.views.properties.PrintablePropertyView;

public interface FormElement extends PrintablePropertyView
{
	/**
	 * @return Type id of this form element.
	 */
	String getElementType();
}
