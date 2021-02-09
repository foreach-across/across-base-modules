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

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRequestResponseLoggingFilter
{
	RequestResponseLoggingFilter requestResponseLoggingFilter = new RequestResponseLoggingFilter( null, false );

	@Test
	void verifyStatusCodes() {
		verify( false, 300, null, 300 );
		verify( false, 300, HttpStatusOperator.LT, 300 );
		verify( false, 300, HttpStatusOperator.GT, 300 );
		verify( false, 300, HttpStatusOperator.NEQ, 300 );

		verify( true, 300, HttpStatusOperator.LTE, 300 );
		verify( true, 300, HttpStatusOperator.EQ, 300 );
		verify( true, 300, HttpStatusOperator.GTE, 300 );

		verify( false, 302, null, 200 );
		verify( false, 302, HttpStatusOperator.LT, 200 );
		verify( true, 302, HttpStatusOperator.GT, 200 );
		verify( true, 302, HttpStatusOperator.NEQ, 200 );

		verify( false, 302, HttpStatusOperator.LTE, 200 );
		verify( false, 302, HttpStatusOperator.EQ, 200 );
		verify( true, 302, HttpStatusOperator.GTE, 200 );

		verify( false, 200, null, 400 );
		verify( true, 200, HttpStatusOperator.LT, 400 );
		verify( false, 200, HttpStatusOperator.GT, 400 );
		verify( true, 200, HttpStatusOperator.NEQ, 400 );

		verify( true, 200, HttpStatusOperator.LTE, 400 );
		verify( false, 200, HttpStatusOperator.EQ, 400 );
		verify( false, 200, HttpStatusOperator.GTE, 400 );

	}

	private void verify( boolean shouldLog, int generatedStatus, HttpStatusOperator configuredOperator, int configuredStatusCode ) {
		MockHttpServletResponse response = new MockHttpServletResponse();
		LogResponseWrapper logResponseWrapper = new LogResponseWrapper( response );
		logResponseWrapper.setStatus( generatedStatus );

		requestResponseLoggingFilter.setHttpStatusCode( configuredStatusCode );
		requestResponseLoggingFilter.setHttpStatusOperator( configuredOperator );
		assertEquals( shouldLog, requestResponseLoggingFilter.shouldLogStatusCode( logResponseWrapper ) );
	}
}