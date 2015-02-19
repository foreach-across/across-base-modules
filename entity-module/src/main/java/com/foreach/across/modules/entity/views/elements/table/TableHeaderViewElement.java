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
package com.foreach.across.modules.entity.views.elements.table;

import com.foreach.across.modules.entity.views.elements.ViewElement;
import com.foreach.across.modules.entity.views.elements.ViewElementSupport;

import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class TableHeaderViewElement extends ViewElementSupport
{
	private TableHeaderCellProcessor cellProcessor;

	public TableHeaderViewElement() {
		setElementType( "table-header" );
	}

	public TableHeaderCellProcessor getCellProcessor() {
		return cellProcessor;
	}

	public void setCellProcessor( TableHeaderCellProcessor cellProcessor ) {
		this.cellProcessor = cellProcessor;
	}

	public Map<String, String> cellProcessor( ViewElement element ) {
		return cellProcessor != null ? cellProcessor.attributes( element ) : null;
	}
}
