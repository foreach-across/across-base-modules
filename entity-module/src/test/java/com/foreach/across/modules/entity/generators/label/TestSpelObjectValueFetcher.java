/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestSpelObjectValueFetcher
{
	@Test
	public void testExpression() {
		TestProperties.Customer customer = new TestProperties.Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		TestProperties.Address address = new TestProperties.Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );

		customer.setAddress( address );

		assertEquals( "some name", new SpelValueFetcher<>( "name" ).getValue( customer ) );
		assertEquals( "some name (123)", new SpelValueFetcher<>( "displayName" ).getValue( customer ) );
		assertEquals( "my street", new SpelValueFetcher<>( "address.street" ).getValue( customer ) );
		assertEquals( 9, new SpelValueFetcher<>( "address.size()" ).getValue( customer ) );
	}
}
