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

package ax.apps;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ActiveProfiles("unsecured")
public class TestUnsecuredApplication extends AbstractApplicationTest
{
	@Disabled
	@Test
	public void healthEndpointReturnsDetails() {
		assertResponseWithStatus( "/health", HttpStatus.OK )
				.contains( "UP" )
				.contains( "diskSpace" );
	}

	@Test
	public void h2consoleIsOpen() {
		assertResponseWithStatus( "/h2-console", HttpStatus.OK );
	}
}

