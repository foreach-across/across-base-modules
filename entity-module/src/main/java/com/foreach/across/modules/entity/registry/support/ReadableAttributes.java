package com.foreach.across.modules.entity.registry.support;

import java.util.Map;

public interface ReadableAttributes
{
	<Y, V extends Y> V getAttribute( Class<Y> attributeType );

	<Y> Y getAttribute( String attributeName );

	<Y> Y getAttribute( String attributeName, Class<Y> attributeType );

	boolean hasAttribute( Class<?> attributeType );

	boolean hasAttribute( String attributeName );

	Map<Object, Object> getAttributes();
}
