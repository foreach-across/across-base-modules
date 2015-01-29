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
package com.foreach.across.modules.it.user;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Refreshable;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.business.AclAuthorities;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.acl.services.QueryableAclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import com.foreach.across.modules.spring.security.infrastructure.aop.AuditableEntityInterceptor;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITUserModule.Config.class, ITAclServices.SecurityConfig.class })
public class ITAclServices
{
	private final TestRepository repository = new TestRepository( 1L );
	private final TestFolder folderOne = new TestFolder( 123 );
	private final TestFile fileInFolderOne = new TestFile( 888 );
	private final TestFolder folderTwo = new TestFolder( 456 );
	private final TestFile fileInFolderTwo = new TestFile( 999 );

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SecuredBean securedBean;

	@Autowired
	private QueryableAclSecurityService acl;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Autowired
	private AclSecurityEntityService aclSecurityEntityService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	private Group group;
	private User userOne, userTwo, userThree, userFour;

	@Before
	public void createUsers() {
		permissionService.definePermission( "manage files", "Manage all files and folders", "unit-test" );
		roleService.defineRole( "ROLE_FILE_MANAGER", "File manager", Arrays.asList( "manage files" ) );

		group = createGroup();

		userOne = createRandomUser( Collections.<Group>emptyList(), Collections.<String>emptyList() );
		userTwo = createRandomUser( Collections.<Group>emptyList(), Collections.singleton( "ROLE_ADMIN" ) );
		userThree = createRandomUser( Collections.<Group>emptyList(), Collections.singleton( "ROLE_FILE_MANAGER" ) );
		userFour = createRandomUser( Collections.singleton( group ), Collections.<String>emptyList() );
	}

	@Test
	public void verifyBootstrapped() {
		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( UserModule.NAME );

		assertNotNull( moduleInfo.getApplicationContext().getBean( GroupAclInterceptor.class ) );
		assertNotNull( acrossContextInfo.getModuleInfo( SpringSecurityInfrastructureModule.NAME )
		                                .getApplicationContext().getBean( AuditableEntityInterceptor.class ) );
	}

	private Group createGroup( String... roles ) {
		Group group = groupService.getGroupById( -999 );

		if ( group == null ) {
			Group dto = new Group();
			dto.setName( RandomStringUtils.randomAscii( 20 ) );
			dto.setNewEntityId( -999L );

			for ( String role : roles ) {
				dto.addRole( roleService.getRole( role ) );
			}

			group = groupService.save( dto );
		}

		return group;
	}

	@After
	public void clearAcls() {
		acl.deleteAcl( repository, true );
	}

	private User createRandomUser( Collection<Group> groups, Collection<String> roles ) {
		User user = new User();
		user.setUsername( UUID.randomUUID().toString() );
		user.setEmail( UUID.randomUUID().toString() + "@test.com" );
		user.setPassword( "test" );
		user.setFirstName( RandomStringUtils.randomAscii( 25 ) );
		user.setLastName( RandomStringUtils.randomAscii( 25 ) );
		user.setDisplayName( RandomStringUtils.randomAscii( 50 ) );

		for ( Group group : groups ) {
			user.addGroup( group );
		}

		for ( String role : roles ) {
			user.addRole( roleService.getRole( role ) );
		}

		return userService.save( user );
	}

	@Test
	public void aclPermissionsShouldHaveBeenInstaller() {
		Role adminRole = roleService.getRole( "ROLE_ADMIN" );

		assertNotNull( adminRole );
		assertTrue( adminRole.hasPermission( AclAuthorities.AUDIT_ACL ) );
		assertTrue( adminRole.hasPermission( AclAuthorities.MODIFY_ACL ) );
		assertTrue( adminRole.hasPermission( AclAuthorities.TAKE_OWNERSHIP ) );
	}

	@Test
	public void userWithDirectApprovalOnFolder() {
		logon( userOne );

		acl.createAcl( repository );
		acl.createAclWithParent( folderOne, repository );
		acl.createAclWithParent( fileInFolderOne, folderOne );
		acl.createAclWithParent( folderTwo, repository );
		acl.createAclWithParent( fileInFolderTwo, folderTwo );

		assertFalse( canRead( folderOne ) );
		assertFalse( canRead( fileInFolderOne ) );

		acl.allow( userOne, folderOne, AclPermission.READ );

		assertTrue( canRead( folderOne ) );
		assertTrue( canRead( fileInFolderOne ) );
		assertFalse( canWrite( folderOne ) );
		assertFalse( canWrite( fileInFolderOne ) );

		logon( userTwo );
		assertFalse( canRead( folderOne ) );
		assertFalse( canRead( fileInFolderOne ) );
	}

	@Test
	public void permissionsThroughAuthorityAndGroup() {
		logon( userOne );

		acl.createAcl( repository );
		acl.createAclWithParent( folderOne, repository );
		acl.createAclWithParent( fileInFolderOne, folderOne );
		acl.createAclWithParent( folderTwo, repository );
		acl.createAclWithParent( fileInFolderTwo, folderTwo );

		acl.allow( "manage files", repository, AclPermission.WRITE );
		acl.allow( "manage files", repository, AclPermission.READ );

		acl.allow( userTwo, folderTwo, AclPermission.READ );
		acl.allow( userOne, fileInFolderOne, AclPermission.WRITE );
		acl.allow( userOne, fileInFolderTwo, AclPermission.WRITE );

		acl.allow( group, repository, AclPermission.WRITE );

		logon( userOne );
		assertFalse( canRead( folderOne ) || canWrite( folderOne ) );
		assertFalse( canRead( folderTwo ) || canWrite( folderTwo ) );
		assertTrue( canWrite( fileInFolderOne ) && !canRead( fileInFolderOne ) );
		assertTrue( canWrite( fileInFolderTwo ) && !canRead( fileInFolderTwo ) );

		logon( userTwo );
		assertFalse( canRead( folderOne ) || canWrite( folderOne ) );
		assertTrue( canRead( folderTwo ) && !canWrite( folderTwo ) );
		assertFalse( canRead( fileInFolderOne ) || canWrite( fileInFolderOne ) );
		assertTrue( canRead( fileInFolderTwo ) && !canWrite( fileInFolderTwo ) );

		logon( userThree );
		assertTrue( canRead( folderOne ) && canWrite( folderOne ) );
		assertTrue( canRead( folderTwo ) && canWrite( folderTwo ) );
		assertTrue( canRead( fileInFolderOne ) && canWrite( fileInFolderOne ) );
		assertTrue( canRead( fileInFolderTwo ) && canWrite( fileInFolderTwo ) );

		logon( userFour );
		assertTrue( !canRead( folderOne ) && canWrite( folderOne ) );
		assertTrue( !canRead( folderTwo ) && canWrite( folderTwo ) );
		assertTrue( !canRead( fileInFolderOne ) && canWrite( fileInFolderOne ) );
		assertTrue( !canRead( fileInFolderTwo ) && canWrite( fileInFolderTwo ) );
	}

	@Test
	public void manualTestingOfPermissions() {
		logon( userOne );

		acl.createAcl( repository );
		acl.createAclWithParent( folderOne, repository );
		acl.createAclWithParent( fileInFolderOne, folderOne );
		acl.createAclWithParent( folderTwo, repository );
		acl.createAclWithParent( fileInFolderTwo, folderTwo );

		acl.allow( "manage files", repository, AclPermission.WRITE );
		acl.allow( "manage files", repository, AclPermission.READ );

		acl.allow( userTwo, folderTwo, AclPermission.READ );
		acl.allow( userOne, fileInFolderOne, AclPermission.WRITE );
		acl.allow( userOne, fileInFolderTwo, AclPermission.WRITE );

		acl.allow( group, repository, AclPermission.WRITE );

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		assertFalse( acl.hasPermission( auth, folderOne, AclPermission.READ ) );
		assertFalse( acl.hasPermission( auth, folderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( auth, folderTwo, AclPermission.READ ) );
		assertFalse( acl.hasPermission( auth, folderTwo, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( auth, fileInFolderOne, AclPermission.READ ) );
		assertTrue( acl.hasPermission( auth, fileInFolderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( auth, fileInFolderTwo, AclPermission.READ ) );
		assertTrue( acl.hasPermission( auth, fileInFolderTwo, AclPermission.WRITE ) );

		assertFalse( acl.hasPermission( userOne, folderOne, AclPermission.READ ) );
		assertFalse( acl.hasPermission( userOne, folderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( userOne, folderTwo, AclPermission.READ ) );
		assertFalse( acl.hasPermission( userOne, folderTwo, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( userOne, fileInFolderOne, AclPermission.READ ) );
		assertTrue( acl.hasPermission( userOne, fileInFolderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( userOne, fileInFolderTwo, AclPermission.READ ) );
		assertTrue( acl.hasPermission( userOne, fileInFolderTwo, AclPermission.WRITE ) );

		logon( userFour );

		auth = SecurityContextHolder.getContext().getAuthentication();
		assertFalse( acl.hasPermission( auth, folderOne, AclPermission.READ ) );
		assertTrue( acl.hasPermission( auth, folderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( auth, folderTwo, AclPermission.READ ) );
		assertTrue( acl.hasPermission( auth, folderTwo, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( auth, fileInFolderOne, AclPermission.READ ) );
		assertTrue( acl.hasPermission( auth, fileInFolderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( auth, fileInFolderTwo, AclPermission.READ ) );
		assertTrue( acl.hasPermission( auth, fileInFolderTwo, AclPermission.WRITE ) );

		assertFalse( acl.hasPermission( userFour, folderOne, AclPermission.READ ) );
		assertTrue( acl.hasPermission( userFour, folderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( userFour, folderTwo, AclPermission.READ ) );
		assertTrue( acl.hasPermission( userFour, folderTwo, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( userFour, fileInFolderOne, AclPermission.READ ) );
		assertTrue( acl.hasPermission( userFour, fileInFolderOne, AclPermission.WRITE ) );
		assertFalse( acl.hasPermission( userFour, fileInFolderTwo, AclPermission.READ ) );
		assertTrue( acl.hasPermission( userFour, fileInFolderTwo, AclPermission.WRITE ) );
	}

	@Test
	public void retrieveObjectIdentitiesForPrincipal() {
		logon( userOne );

		acl.createAcl( repository );
		acl.createAclWithParent( folderOne, repository );
		acl.createAclWithParent( fileInFolderOne, folderOne );
		acl.createAclWithParent( folderTwo, repository );
		acl.createAclWithParent( fileInFolderTwo, folderTwo );

		acl.allow( "manage files", repository, AclPermission.READ );
		acl.allow( userTwo, folderTwo, AclPermission.READ );
		acl.allow( userOne, fileInFolderOne, AclPermission.READ );
		acl.allow( userOne, fileInFolderTwo, AclPermission.WRITE );

		acl.allow( group, repository, AclPermission.WRITE );

		Collection<ObjectIdentity> identities = acl.getObjectIdentitiesWithAclEntriesForPrincipal( userOne );
		assertEquals( 2, identities.size() );
		assertTrue( identities.contains( new ObjectIdentityImpl( fileInFolderOne.getClass(),
		                                                         fileInFolderOne.getId() ) ) );
		assertTrue( identities.contains( new ObjectIdentityImpl( fileInFolderTwo.getClass(),
		                                                         fileInFolderTwo.getId() ) ) );

		identities = acl.getObjectIdentitiesWithAclEntriesForPrincipal( userTwo );
		assertEquals( 1, identities.size() );
		assertTrue( identities.contains( new ObjectIdentityImpl( folderTwo.getClass(), folderTwo.getId() ) ) );
	}

	@Test
	public void testGroups() {
		logon( userOne );
		Group group1 = new Group();
		group1.setName( "Test-group" );
		Group savedGroup = groupService.save( group1 );

		assertNotNull( groupService.getGroupById( savedGroup.getId() ) );
		MutableAcl ownAcl = acl.getAcl( savedGroup );
		assertNotNull( ownAcl );
		Long parentAclId = (Long) ownAcl.getParentAcl().getObjectIdentity().getIdentifier();
		AclSecurityEntity parentEntity = aclSecurityEntityService.getSecurityEntityById( parentAclId );
		assertNotNull( parentEntity );
		assertEquals( parentEntity.getName(), "groups" );

		groupService.delete( savedGroup.getId() );
		assertNull( groupService.getGroupById( savedGroup.getId() ) );
		assertNull( acl.getAcl( savedGroup ) );

		AclSecurityEntity groupsSecurityEntity = aclSecurityEntityService.getSecurityEntityByName( "groups" );
		assertNotNull( groupsSecurityEntity );
		assertNotNull( groupsSecurityEntity.getCreatedDate() );
		assertNotNull( groupsSecurityEntity.getCreatedBy() );
		assertNotNull( groupsSecurityEntity.getLastModifiedDate() );
		assertNotNull( groupsSecurityEntity.getLastModifiedBy() );

		//Not sure if I should be doing this here...
		acrossContextInfo.getModuleInfo( UserModule.NAME ).getModule().setProperty(
				UserModuleSettings.ENABLE_DEFAULT_ACLS, false );
		Group group2Dto = new Group();
		group2Dto.setName( "Test-group-2" );
		Group savedGroup2 = groupService.save( group2Dto );

		assertNotNull( groupService.getGroupById( savedGroup2.getId() ) );
		assertNull( acl.getAcl( savedGroup2 ) );

		groupService.delete( savedGroup2.getId() );
		assertNull( groupService.getGroupById( savedGroup2.getId() ) );
		assertNull( acl.getAcl( savedGroup2 ) );
		assertNotNull( aclSecurityEntityService.getSecurityEntityByName( "groups" ) );

		//Back to the original situation
		acrossContextInfo.getModuleInfo( UserModule.NAME ).getModule().setProperty(
				UserModuleSettings.ENABLE_DEFAULT_ACLS, true );
	}

	@Test
	public void usersShouldHaveAuditInfo() {
		securityPrincipalService.authenticate( machinePrincipalService.getMachinePrincipalByName( "system" ) );

		User createdUser = createRandomUser( new ArrayList<Group>(), new ArrayList<String>() );
		assertNotNull( createdUser.getCreatedDate() );
		assertNotNull( createdUser.getCreatedBy() );
		Date lastModifiedDate = createdUser.getLastModifiedDate();
		assertNotNull( lastModifiedDate );
		assertNotNull( createdUser.getLastModifiedBy() );

		User createdUserDto = createdUser.toDto();
		createdUserDto.setLastName( "foo" );
		userService.save( createdUserDto );

		createdUser = createRandomUser( new ArrayList<Group>(), new ArrayList<String>() );
		assertNotNull( createdUser.getCreatedDate() );
		assertNotNull( createdUser.getCreatedBy() );
		assertNotNull( createdUser.getLastModifiedDate() );
		assertNotNull( createdUser.getLastModifiedBy() );
		assertNotEquals( lastModifiedDate, createdUser.getLastModifiedDate() );
	}

	private boolean canRead( Object object ) {
		try {
			return securedBean.canRead( object );
		}
		catch ( AccessDeniedException ade ) {
			return false;
		}
	}

	private boolean canWrite( Object object ) {
		try {
			return securedBean.canWrite( object );
		}
		catch ( AccessDeniedException ade ) {
			return false;
		}
	}

	private void logon( User user ) {
		securityPrincipalService.authenticate( user );
	}

	@Configuration
	protected static class SecurityConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.getModule( UserModule.NAME ).setProperty( UserModuleSettings.ENABLE_DEFAULT_ACLS, true );
			context.addModule( new SpringSecurityAclModule() );
			context.addModule( testModule() );
		}

		public AcrossModule testModule() {
			EmptyAcrossModule emptyAcrossModule = new EmptyAcrossModule( "TestModule" );
			emptyAcrossModule.addApplicationContextConfigurer( TestModuleConfig.class );

			return emptyAcrossModule;
		}
	}

	@Configuration
	protected static class TestModuleConfig
	{
		@Bean
		@Exposed
		public SecuredBean securedBean() {
			return new SecuredBean();
		}
	}

	protected static class TestFile implements IdBasedEntity
	{
		private final long id;

		public TestFile( long id ) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}
	}

	public static class TestFolder extends TestFile
	{
		public TestFolder( long id ) {
			super( id );
		}
	}

	public static class TestRepository extends TestFile
	{
		public TestRepository( long id ) {
			super( id );
		}
	}

	@Refreshable
	public static class SecuredBean
	{
		@PreAuthorize("hasPermission(#fileOrFolder, 'READ')")
		public boolean canRead( Object fileOrFolder ) {
			return true;
		}

		@PreAuthorize("hasPermission(#fileOrFolder, 'WRITE')")
		public boolean canWrite( Object fileOrFolder ) {
			return true;
		}
	}
}

