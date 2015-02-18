package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.views.processors.NoOpRowProcessor;
import com.foreach.across.modules.entity.views.processors.RowProcessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;

/**
 * Model for a basic list view.
 *
 * @see EntityListViewFactory
 */
public class EntityListView extends EntityView
{
	public static final String VIEW_NAME = "listView";
	public static final String VIEW_TEMPLATE = "th/entity/list";

	public static final String ATTRIBUTE_PAGEABLE = "pageable";
	public static final String ATTRIBUTE_PAGE = "page";
	public static final String ATTRIBUTE_SHOW_RESULT_NUMBER = "showResultNumber";
	public static final String ATTRIBUTE_ROW_PROCESSOR = "rowProcessor";

	public EntityListView( ModelMap model ) {
		super( model );

		if ( !containsAttribute( ATTRIBUTE_SHOW_RESULT_NUMBER ) ) {
			setShowResultNumber( true );
		}
		if ( !containsAttribute( ATTRIBUTE_ROW_PROCESSOR ) ) {
			setRowProcessor( new NoOpRowProcessor() );
		}
	}

	public Pageable getPageable() {
		return getAttribute( ATTRIBUTE_PAGEABLE );
	}

	public void setPageable( Pageable pageable ) {
		addAttribute( ATTRIBUTE_PAGEABLE, pageable );
	}

	public Page getPage() {
		return getAttribute( ATTRIBUTE_PAGE );
	}

	public void setPage( Page page ) {
		addAttribute( ATTRIBUTE_PAGE, page );
	}

	public boolean isShowResultNumber() {
		return getAttribute( ATTRIBUTE_SHOW_RESULT_NUMBER );
	}

	public void setShowResultNumber( boolean showResultNumber ) {
		addAttribute( ATTRIBUTE_SHOW_RESULT_NUMBER, showResultNumber );
	}

	public void setRowProcessor( RowProcessor rowProcessor ) {
		addAttribute( ATTRIBUTE_ROW_PROCESSOR, rowProcessor );
	}

	public RowProcessor getRowProcessor() {
		return getAttribute( ATTRIBUTE_ROW_PROCESSOR );
	}
}
