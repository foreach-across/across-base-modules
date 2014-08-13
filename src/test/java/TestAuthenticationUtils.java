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
