package com.foreach.across.modules.entity.registry.support;

import java.util.Map;

public interface WritableAttributes
{
	<Y> void addAttribute( Class<Y> attributeType, Y attributeValue );

	void addAttribute( String attributeName, Object attributeValue );

	void addAllAttributes( Map<Object, Object> attributes );

	<Y> Y removeAttribute( Class<Y> attributeType );

	<Y> Y removeAttribute( String attributeName );
}
