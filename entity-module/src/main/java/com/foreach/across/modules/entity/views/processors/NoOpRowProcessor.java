package com.foreach.across.modules.entity.views.processors;

/**
 * @author niels
 * @since 6/02/2015
 */
public class NoOpRowProcessor<T> implements RowProcessor<T> {
    @Override
    public String process(T entity) {
        return null;
    }
}
