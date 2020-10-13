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

package com.foreach.across.modules.spring.security.infrastructure.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSecurityPrincipalId
{
	private final static String PRINCIPAL_ID = "someUserId";

	@Test
	public void sameHashCode() {
		assertEquals( SecurityPrincipalId.of( PRINCIPAL_ID ).hashCode(), SecurityPrincipalId.of( PRINCIPAL_ID ).hashCode() );
		assertThat( SecurityPrincipalId.of( "otherUserId" ).hashCode() ).isNotEqualTo( SecurityPrincipalId.of( PRINCIPAL_ID ).hashCode() );
	}

	@Test
	public void equalsBasedOnId() {
		assertEquals( SecurityPrincipalId.of( PRINCIPAL_ID ), SecurityPrincipalId.of( PRINCIPAL_ID ) );
		assertThat( SecurityPrincipalId.of( "otherUserId" ) ).isNotEqualTo( SecurityPrincipalId.of( PRINCIPAL_ID ) );
	}

	@Test
	public void nullValueIsNotAllowed() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			SecurityPrincipalId.of( null );
		} );
	}

	@Test
	public void blankStringIsNotAllowed() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			SecurityPrincipalId.of( "" );
		} );
	}

	@Test
	public void testToString() {
		assertThat( SecurityPrincipalId.of( "123" ).toString() ).isEqualTo( "123" );
	}

	@Test
	public void serialization() {
		SecurityPrincipalId id = SecurityPrincipalId.of( "1@@@john.doe" );
		byte[] data = SerializationUtils.serialize( id );
		assertThat( SerializationUtils.<Object>deserialize( data ) ).isEqualTo( id );
	}

	@Test
	@SneakyThrows
	public void jsonSerializedAsString() {
		SecurityPrincipalId id = SecurityPrincipalId.of( "1@@@john.doe" );

		ObjectMapper om = new ObjectMapper();
		assertThat( om.writeValueAsString( id ) ).isEqualTo( "\"1@@@john.doe\"" );
		assertThat( om.readValue( "\"2@@@jane.doe\"", SecurityPrincipalId.class ) ).isEqualTo( SecurityPrincipalId.of( "2@@@jane.doe" ) );
	}
}
