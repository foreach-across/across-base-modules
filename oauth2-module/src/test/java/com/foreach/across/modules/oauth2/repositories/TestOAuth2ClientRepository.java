package com.foreach.across.modules.oauth2.repositories;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestOAuth2ClientRepository.Config.class)
@DirtiesContext
public class TestOAuth2ClientRepository
{
	@Autowired
	private OAuth2Service oAuth2Service;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PermissionService permissionService;

	@Before
	public void createRolesAndPermissions() {
		permissionService.definePermission( "perm one", "", "test-perms" );
		permissionService.definePermission( "perm two", "", "test-perms" );
		permissionService.definePermission( "perm three", "", "test-perms" );

		roleService.defineRole( "role one", "", Arrays.asList( "perm one", "perm two" ) );
		roleService.defineRole( "role two", "", Arrays.asList( "perm two", "perm three" ) );
	}

	@Test
	public void clientNotFound() {
		OAuth2Client bla = oAuth2Service.getClientById( "-4" );
		assertNull( bla );
	}

	@Test
	public void clientWithoutPermissions() {
		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "fredClient" );
		oAuth2Client.setClientSecret( "fred" );
		oAuth2Client.setSecretRequired( true );

		oAuth2Client = oAuth2Service.saveClient( oAuth2Client );

		assertNotNull( oAuth2Client.getClientId() );
		assertTrue( oAuth2Client.getId() > 0 );

		OAuth2Client existing = oAuth2Service.getClientById( oAuth2Client.getClientId() );

		assertEquals( oAuth2Client.getClientId(), existing.getClientId() );
		assertEquals( oAuth2Client.getClientSecret(), existing.getClientSecret() );
		assertEquals( oAuth2Client.isSecretRequired(), existing.isSecretRequired() );
	}

	@Test
	public void clientWithRoles() {
		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "test" );
		oAuth2Client.setClientSecret( "secret" );
		oAuth2Client.setSecretRequired( true );
		oAuth2Client.getRoles().add( roleService.getRole( "role one" ) );
		oAuth2Client.getRoles().add( roleService.getRole( "role two" ) );

		oAuth2Service.saveClient( oAuth2Client );

		OAuth2Client existing = oAuth2Service.getClientById( oAuth2Client.getClientId() );

		assertEquals( oAuth2Client.getRoles(), existing.getRoles() );
	}

	@Test
	public void clientWithScopes() {
		OAuth2Scope oAuth2Scope = new OAuth2Scope();
		oAuth2Scope.setName( "testScope" );
		oAuth2Service.saveScope( oAuth2Scope );

		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "test2" );
		oAuth2Client.setClientSecret( "secret" );
		oAuth2Client.setSecretRequired( true );

		oAuth2Service.saveClient( oAuth2Client );

		OAuth2Scope scope = oAuth2Service.getScopeById( oAuth2Scope.getId() );
		OAuth2ClientScope oAuth2ClientScope = new OAuth2ClientScope();
		oAuth2ClientScope.setOAuth2Scope( scope );
		oAuth2ClientScope.setOAuth2Client( oAuth2Client );
		oAuth2Client.getOAuth2ClientScopes().add( oAuth2ClientScope );

		oAuth2Service.saveClient( oAuth2Client );

		OAuth2Client existing = oAuth2Service.getClientById( oAuth2Client.getClientId() );

		Set<String> existingScope = existing.getScope();
		assertTrue( existingScope.contains( "testScope" ) );
	}

	@Test
	public void clientWithResourceIds() {
		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "test3" );
		oAuth2Client.setClientSecret( "secret" );
		oAuth2Client.setSecretRequired( true );
		Set<String> resourceIds = oAuth2Client.getResourceIds();
		resourceIds.add( "resourceId1" );
		resourceIds.add( "resourceId2" );

		oAuth2Service.saveClient( oAuth2Client );

		OAuth2Client existing = oAuth2Service.getClientById( oAuth2Client.getClientId() );

		assertEquals( resourceIds, existing.getResourceIds() );
	}

	@Test
	public void clientWithGrantTypes() {
		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "test4" );
		oAuth2Client.setClientSecret( "secret" );
		oAuth2Client.setSecretRequired( true );
		Set<String> authorizedGrantTypes = oAuth2Client.getAuthorizedGrantTypes();
		authorizedGrantTypes.add( "auth1" );
		authorizedGrantTypes.add( "auth2" );

		oAuth2Service.saveClient( oAuth2Client );

		OAuth2Client existing = oAuth2Service.getClientById( oAuth2Client.getClientId() );

		assertEquals( authorizedGrantTypes, existing.getAuthorizedGrantTypes() );
	}

	@Test
	public void clientWithRegisteredRedirectUri() {
		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "test5" );
		oAuth2Client.setClientSecret( "secret" );
		oAuth2Client.setSecretRequired( true );
		Set<String> registeredRedirectUri = oAuth2Client.getRegisteredRedirectUri();
		registeredRedirectUri.add( "auth1" );
		registeredRedirectUri.add( "auth2" );

		oAuth2Service.saveClient( oAuth2Client );

		OAuth2Client existing = oAuth2Service.getClientById( oAuth2Client.getClientId() );

		assertEquals( registeredRedirectUri, existing.getRegisteredRedirectUri() );
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossHibernateJpaModule() );
			context.addModule( userModule() );
			context.addModule( propertiesModule() );
			context.addModule( new SpringSecurityModule() );
			context.addModule( new OAuth2Module() );
		}

		private PropertiesModule propertiesModule() {
			return new PropertiesModule();
		}

		private UserModule userModule() {
			return new UserModule();
		}
	}
}