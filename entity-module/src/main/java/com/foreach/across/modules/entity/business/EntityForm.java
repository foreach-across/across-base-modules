package com.foreach.across.modules.entity.business;



import com.foreach.across.modules.entity.views.elements.ViewElement;

import java.util.Collection;
import java.util.LinkedList;

@Deprecated
public class EntityForm
{
	private Collection<ViewElement> elements = new LinkedList<>();

	public void setEntity( Object instance ) {
		/*for ( FormElement element : elements ) {
			element.setValue(
					EntityUtils.getPropertyValue(
							BeanUtils.getPropertyDescriptor( ClassUtils.getUserClass( instance.getClass() ),
							                                 element.getName() ),
							instance
					)
			);
		}*/
	}

	public void addElement( ViewElement formElement ) {
		elements.add( formElement );
	}

	public Collection<ViewElement> getElements() {
		return elements;
	}
}
