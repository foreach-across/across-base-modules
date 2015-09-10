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
import com.foreach.across.modules.spring.security.AuthenticationUtils;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthenticationUtils
{
	@Test
	public void testInvalidAuthorityReturnsFalse() throws Exception {
		assertEquals( false, AuthenticationUtils.hasAuthority( null, "foezoj" ) );
	}

	@Test
	public void testAuthorityWithNullAuthoritiesReturnsFalse() throws Exception {
		assertEquals( false, AuthenticationUtils.hasAuthority( mock( Authentication.class ), "foezoj" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAuthorityWithAuthoritiesAndNullAuthorityReturnsFalse() throws Exception {
		Authentication authentication = mock( Authentication.class );
		Collection grantedAuthorities = Sets.newSet( null, new SimpleGrantedAuthority( "bla" ) );
		when( authentication.getAuthorities() ).thenReturn( grantedAuthorities );
		assertEquals( false, AuthenticationUtils.hasAuthority( authentication, null ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAuthorityWithAuthoritiesAndNullAuthorityReturnsTrue() throws Exception {
		Authentication authentication = mock( Authentication.class );
		Collection grantedAuthorities = Sets.newSet( null, new SimpleGrantedAuthority( "bla" ) );
		when( authentication.getAuthorities() ).thenReturn( grantedAuthorities );
		assertEquals( true, AuthenticationUtils.hasAuthority( authentication, "bla" ) );
	}
}
