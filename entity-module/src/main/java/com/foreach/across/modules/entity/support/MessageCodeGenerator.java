package com.foreach.across.modules.entity.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Generates message codes in order, according to a root collection, sub collection and item key.
 * The order of the collections will determine the message code lookup order.
 * <p/>
 * Example:
 * * root collections: One, Two
 * * sub collections: List, Table
 * * item key: Property
 * <p/>
 * This will result in the following message codes - in order:
 * * One.List.Property
 * * One.Table.Property
 * * One.Property
 * * Two.List.Property
 * * Two.Table.Property
 * * Two.Property
 */
public class MessageCodeGenerator
{
	private String[] rootCollections;
	private String itemKey;

	public MessageCodeGenerator( String[] rootCollections, String itemKey ) {
		setRootCollections( rootCollections );
		setItemKey( itemKey );
	}

	public String[] getRootCollections() {
		return rootCollections;
	}

	public void setRootCollections( String[] rootCollections ) {
		Assert.notNull( rootCollections );
		this.rootCollections = rootCollections;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey( String itemKey ) {
		Assert.notNull( itemKey );
		this.itemKey = itemKey;
	}

	public String[] defaultCodes() {
		return MessageCodeGenerator.generateCodes( rootCollections, new String[0], itemKey );
	}

	public String[] forSubCollections( String... subCollections ) {
		return MessageCodeGenerator.generateCodes( rootCollections, subCollections, itemKey );
	}

	public static String[] generateCodes( String[] rootCollections, String[] subCollections, String itemKey ) {
		Assert.notNull( rootCollections );
		Assert.notNull( subCollections );
		Assert.notNull( itemKey );

		if ( rootCollections.length == 0 && subCollections.length == 0 ) {
			return new String[] { itemKey };
		}

		int index = 0;

		String[] codes;

		if ( rootCollections.length == 0 ) {
			codes = new String[subCollections.length + 1];

			for ( String subCollection : subCollections ) {
				codes[index++] = subCollection + "." + itemKey;
			}
			codes[index] = itemKey;
		}
		else {
			codes = new String[( subCollections.length + 1 ) * rootCollections.length];

			for ( String rootCollection : rootCollections ) {
				String prefix = StringUtils.isEmpty( rootCollection ) ? "" : rootCollection + ".";
				for ( String subCollection : subCollections ) {
					codes[index++] = prefix + subCollection + "." + itemKey;
				}
				codes[index++] = prefix + itemKey;
			}
		}

		return codes;
	}
}
