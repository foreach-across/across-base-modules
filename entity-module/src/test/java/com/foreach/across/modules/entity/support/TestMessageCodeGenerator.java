package com.foreach.across.modules.entity.support;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestMessageCodeGenerator
{
	@Test
	public void staticGenerationMultiple() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities" };
		String[] subCollections = new String[] { "views.listView", "views" };
		String itemKey = "properties.displayName";

		String[] generated = MessageCodeGenerator.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void emptyRootCollection() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities", "" };
		String[] subCollections = new String[] { "views.listView", "views" };
		String itemKey = "properties.displayName";

		String[] generated = MessageCodeGenerator.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName",
						"views.listView.properties.displayName",
						"views.properties.displayName",
						"properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationRootCollectionOnly() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities" };
		String[] subCollections = new String[0];
		String itemKey = "properties.displayName";

		String[] generated = MessageCodeGenerator.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationSingle() {
		String[] rootCollections = new String[] { "UserModule.entities.user" };
		String[] subCollections = new String[] { "views.listView" };
		String itemKey = "properties.displayName";

		String[] generated = MessageCodeGenerator.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void subCollectionGenerator() {
		MessageCodeGenerator generator = new MessageCodeGenerator(
				new String[] { "UserModule.entities.user", "EntityModule.entities" },
		        "properties.displayName"
		);

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generator.defaultCodes()
		);

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generator.forSubCollections( "views.listView", "views" )
		);
	}
}
