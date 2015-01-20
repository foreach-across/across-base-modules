package com.foreach.across.modules.entity.views;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

	public EntityListView() {
		setShowResultNumber( true );
	}

	public Pageable getPageable() {
		return (Pageable) getModelMap().get( ATTRIBUTE_PAGEABLE );
	}

	public void setPageable( Pageable pageable ) {
		getModelMap().put( ATTRIBUTE_PAGEABLE, pageable );
	}

	public Page getPage() {
		return (Page) getModelMap().get( ATTRIBUTE_PAGE );
	}

	public void setPage( Page page ) {
		getModelMap().put( ATTRIBUTE_PAGE, page );
	}

	public boolean isShowResultNumber() {
		return (Boolean) getModelMap().get( ATTRIBUTE_SHOW_RESULT_NUMBER );
	}

	public void setShowResultNumber( boolean showResultNumber ) {
		getModelMap().put( ATTRIBUTE_SHOW_RESULT_NUMBER, showResultNumber );
	}
}
