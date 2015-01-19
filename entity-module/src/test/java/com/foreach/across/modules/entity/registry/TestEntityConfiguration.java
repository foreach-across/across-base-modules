package com.foreach.across.modules.entity.registry;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TestEntityConfiguration
{
	@Test
	public void defaultNameAndDisplayName() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( BigDecimal.class );

		assertEquals( "bigDecimal", config.getName() );
		assertEquals( "Big decimal", config.getDisplayName() );
	}

	@Test
	public void configuredNameAndDefaultDisplayName() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( "someOtherName", BigDecimal.class );

		assertEquals( "someOtherName", config.getName() );
		assertEquals( "Some other name", config.getDisplayName() );
	}

	@Test
	public void customDisplayName() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( "someOtherName", BigDecimal.class );
		config.setDisplayName( "Display name" );

		assertEquals( "someOtherName", config.getName() );
		assertEquals( "Display name", config.getDisplayName() );
	}
}
