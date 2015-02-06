package com.foreach.across.modules.entity.views.processors;

/**
 * @author niels
 * @since 6/02/2015
 */
public interface RowProcessor<T> {
    String process(T entity);
}
