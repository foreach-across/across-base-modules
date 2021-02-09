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

package com.foreach.across.modules.logging.requestresponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.compare.ComparableUtils;
import org.apache.commons.lang3.compare.ComparableUtils.ComparableCheckBuilder;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum HttpStatusOperator
{
	LT( "less than", ComparableCheckBuilder::lessThan ),
	LTE( "less than or equal to", ComparableCheckBuilder::lessThanOrEqualTo ),
	EQ( "equal to", ComparableCheckBuilder::equalTo ),
	NEQ( "not equal to", ( status1, status2 ) -> !status1.equalTo( status2 ) ),
	GT( "greater than", ComparableCheckBuilder::greaterThan ),
	GTE( "greater than or equal to", ComparableCheckBuilder::greaterThanOrEqualTo );

	@Getter
	private final String humanString;
	private final BiFunction<ComparableCheckBuilder<Integer>, Integer, Boolean> predicate;

	public boolean compare( int status, Integer httpStatusCode ) {
		return predicate.apply( ComparableUtils.is( status ), httpStatusCode ).booleanValue();
	}
}
