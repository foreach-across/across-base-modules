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
package com.foreach.across.modules.entity.views.elements;

import com.foreach.across.modules.entity.views.elements.button.ButtonViewElement;
import com.foreach.across.modules.entity.views.elements.container.ContainerViewElement;
import com.foreach.across.modules.entity.views.elements.fieldset.FieldsetViewElement;
import com.foreach.across.modules.entity.views.elements.form.checkbox.CheckboxFormElement;
import com.foreach.across.modules.entity.views.elements.form.date.DateFormElement;
import com.foreach.across.modules.entity.views.elements.form.hidden.HiddenFormElement;
import com.foreach.across.modules.entity.views.elements.form.select.SelectFormElement;
import com.foreach.across.modules.entity.views.elements.form.textbox.TextboxFormElement;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 */
public class TestViewElementsHelper
{
	private ViewElementsHelper elements = new ViewElementsHelper();

	@Test
	public void noCollections() {
		assertFalse( elements.isCollection( new TextboxFormElement() ) );
		assertFalse( elements.isCollection( new ButtonViewElement() ) );
		assertFalse( elements.isCollection( new CheckboxFormElement() ) );
		assertFalse( elements.isCollection( new DateFormElement() ) );
		assertFalse( elements.isCollection( new HiddenFormElement() ) );
		assertFalse( elements.isCollection( new SelectFormElement() ) );
	}

	@Test
	public void commonCollectionTypes() {
		assertTrue( elements.isCollection( new ContainerViewElement() ) );
		assertTrue( elements.isCollection( new FieldsetViewElement() ) );
	}

	@Test
	public void hasChildrenReturnsFalseIfNoCollection() {
		assertFalse( elements.hasChildren( new TextboxFormElement() ) );
		assertFalse( elements.hasChildren( new ButtonViewElement() ) );
	}

	@Test
	public void hasChildrenReturnsFalseIfEmptyCollection() {
		assertFalse( elements.hasChildren( new ButtonViewElement() ) );
	}

	@Test
	public void hasChildrenReturnsTrueIfNotEmptyCollection() {
		ContainerViewElement container = new ContainerViewElement();
		container.add( new ButtonViewElement() );

		assertTrue( elements.hasChildren( container ) );
	}
}
