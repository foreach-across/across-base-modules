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
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class TableViewElement extends ViewElementSupport
{
	private TableHeaderViewElement header;
	private Page page;
	private Iterable<ViewElement> columns;

	private boolean showResultNumber;

	private TableRowProcessor rowProcessor = new TableRowProcessor()
	{
		@Override
		public Map<String, String> attributes( Object entity ) {
			return null;
		}
	};

	private TableCellProcessor cellProcessor = new TableCellProcessor()
	{
		@Override
		public Map<String, String> attributes( ViewElement column, Object entity ) {
			return null;
		}
	};

	public TableViewElement() {
		setElementType( "table" );

		showResultNumber = true;
		header = new TableHeaderViewElement();
	}

	public boolean isShowResultNumber() {
		return showResultNumber;
	}

	public void setShowResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
	}

	public TableHeaderViewElement getHeader() {
		return header;
	}

	public void setHeader( TableHeaderViewElement header ) {
		this.header = header;
	}

	public TableRowProcessor getRowProcessor() {
		return rowProcessor;
	}

	public void setRowProcessor( TableRowProcessor rowProcessor ) {
		this.rowProcessor = rowProcessor;
	}

	public TableCellProcessor getCellProcessor() {
		return cellProcessor;
	}

	public void setCellProcessor( TableCellProcessor cellProcessor ) {
		this.cellProcessor = cellProcessor;
	}

	public Page getPage() {
		return page;
	}

	public void setPage( Page page ) {
		this.page = page;
	}

	public Iterable<ViewElement> getColumns() {
		return columns;
	}

	public void setColumns( Iterable<ViewElement> columns ) {
		this.columns = columns;
	}

	public List getRows() {
		return page.getContent();
	}

	public Map<String, String> rowProcessor( Object entity ) {
		return rowProcessor.attributes( entity );
	}

	public Map<String, String> cellProcessor( ViewElement column, Object entity ) {
		return cellProcessor.attributes( column, entity );
	}
}
