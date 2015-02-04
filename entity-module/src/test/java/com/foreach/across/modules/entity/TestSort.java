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
package com.foreach.across.modules.entity;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Unit test that verifies the behavior of Sort has not been changed from Spring Data.
 * An empty sort is required as otherwise there is a possibility of nullpointer on default pageable.
 *
 * @author Arne Vandamme
 */
public class TestSort
{
	@Test
	public void springDataSortBehavior() {
		List<Sort.Order> orders = new ArrayList<>( 1 );
		orders.add( new Sort.Order( "name" ) );

		Sort emptySort = new Sort( orders );

		orders.clear();

		assertFalse( emptySort.iterator().hasNext() );
	}
}
