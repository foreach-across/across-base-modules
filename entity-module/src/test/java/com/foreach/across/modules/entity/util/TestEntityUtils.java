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
package com.foreach.across.modules.entity.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestEntityUtils
{
	@Test
	public void propertyDisplayName() {
		assertEquals( "Name", EntityUtils.propertyToDisplayName( "name" ) );
		assertEquals( "Principal name", EntityUtils.propertyToDisplayName( "principalName" ));
		assertEquals( "Address street", EntityUtils.propertyToDisplayName( "address.street" ) );
		assertEquals( "Customer address zip code", EntityUtils.propertyToDisplayName( "customer.address.zipCode" ) );
		assertEquals( "Groups size", EntityUtils.propertyToDisplayName( "groups.size()" ) );
		assertEquals( "Text with html", EntityUtils.propertyToDisplayName( "textWithHTML" ) );
		assertEquals( "Members 0 length", EntityUtils.propertyToDisplayName( "members[0].length" ) );
		assertEquals( "Generated label", EntityUtils.propertyToDisplayName( "Generated label" ) );
	}
}
