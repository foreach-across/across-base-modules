package com.foreach.across.modules.entity.registry.properties.meta;

/**
 * @author niels
 * @since 4/02/2015
 */
public class PropertyPersistenceMetadata
{

	private boolean embedded;

	public void setEmbedded( boolean embedded ) {
		this.embedded = embedded;
	}

	public boolean isEmbedded() {
		return embedded;
	}
}
