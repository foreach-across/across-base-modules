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
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.business.AclAuthorities;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.*;
import com.foreach.across.test.AcrossTestConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.EnumSet;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITUserModule.Config.class)
public class ITUserModule
{
	@Autowired
	private GroupService groupService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Test
	public void verifyBootstrapped() {
		assertNotNull( userService );
		User admin = userService.getUserByUsername( "admin" );
		assertNotNull( admin );
		assertEquals( "admin", admin.getUsername() );
		assertEquals( EnumSet.noneOf( UserRestriction.class ), admin.getRestrictions() );
		assertEquals( false, admin.isDeleted() );
		assertEquals( true, admin.getEmailConfirmed() );

		assertEquals( true, admin.isEnabled() );
		assertEquals( true, admin.isAccountNonExpired() );
		assertEquals( true, admin.isAccountNonLocked() );
		assertEquals( true, admin.isCredentialsNonExpired() );

		MachinePrincipal machine = machinePrincipalService.getMachinePrincipalByName( "system" );
		assertNotNull( machine );

		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( UserModule.NAME );

		try {
			assertNull( moduleInfo.getApplicationContext().getBean( GroupAclInterceptor.class ) );
		}
		catch ( NoSuchBeanDefinitionException e ) {
			assertTrue( true ); //If we get this exception, the desired result has been achieved.
		}
	}

	@Test
	public void aclInstallerShouldNotHaveRun() {
		Role adminRole = roleService.getRole( "ROLE_ADMIN" );

		assertNotNull( adminRole );
		assertFalse( adminRole.hasPermission( AclAuthorities.AUDIT_ACL ) );
		assertFalse( adminRole.hasPermission( AclAuthorities.MODIFY_ACL ) );
		assertFalse( adminRole.hasPermission( AclAuthorities.TAKE_OWNERSHIP ) );
	}

	@Test
	public void newlyCreatedUsersHavePositiveIds() {
		User user = new User();
		user.setUsername( RandomStringUtils.random( 10, 33, 127, false, false ) );
		user.setEmail( RandomStringUtils.randomAlphanumeric( 63 ) + "@" + RandomStringUtils.randomAlphanumeric(
				63 ) + ".com" );
		user.setPassword( RandomStringUtils.randomAscii( 30 ) );
		user.setFirstName( RandomStringUtils.randomAscii( 25 ) + "明美" );
		user.setLastName( RandomStringUtils.randomAscii( 25 ) + "明美" );
		user.setDisplayName( RandomStringUtils.randomAscii( 50 ) + "明美" );

		userService.save( user );

		assertTrue( user.getId() > 0 );

		User existing = userService.getUserById( user.getId() );
		assertEquals( user.getUsername(), existing.getUsername() );
		assertEquals( user.getFirstName(), existing.getFirstName() );
		assertEquals( user.getLastName(), existing.getLastName() );
		assertEquals( user.getDisplayName(), existing.getDisplayName() );
		assertNotEquals( user.getPassword(), existing.getPassword() );
	}

	@Test
	public void usersCanHaveNegativeIds() {
		User existing = userService.getUserById( -100 );
		assertNull( existing );

		User user = new User();
		user.setNewEntityId( -100L );
		user.setUsername( "test-user:-100" );
		user.setEmail( "negemail@test.com" );
		user.setPassword( "test password" );
		user.setFirstName( "Test" );
		user.setLastName( "User" );
		user.setDisplayName( "Display name for test user" );

		userService.save( user );

		existing = userService.getUserById( -100 );
		assertNotNull( existing );

		assertEquals( "test-user:-100", existing.getUsername() );
		assertEquals( "Test", existing.getFirstName() );
		assertEquals( "User", existing.getLastName() );
		assertEquals( "Display name for test user", existing.getDisplayName() );
	}

	@Test
	public void propertiesCanOnlyBeSavedForExistingUser() {
		User unsaved = new User();
		unsaved.setId( -9999L );

		UserProperties userProperties = userService.getProperties( unsaved );
		userProperties.put( "test", "test" );

		boolean failed = false;

		try {
			userService.saveProperties( userProperties );
		}
		catch ( Exception e ) {
			failed = true;
		}

		assertTrue( failed );

		User admin = userService.getUserByUsername( "admin" );
		UserProperties adminProperties = userService.getProperties( admin );
		adminProperties.put( "admin", "test" );

		userService.saveProperties( adminProperties );

		UserProperties fetched = userService.getProperties( admin );
		assertEquals( "test", fetched.getValue( "admin" ) );
	}

	@Test
	public void queryDslUserFinding() {
		QUser user = QUser.user;

		Page<User> found = userService.findUsers( user.email.eq( "some@email.com" ), new QPageRequest( 0, 10 ) );
		assertEquals( 0, found.getTotalElements() );
		assertEquals( 0, found.getTotalPages() );

		User created = new User();
		created.setUsername( "some@email.com" );
		created.setEmail( "some@email.com" );
		created.setPassword( "test password" );
		created.setFirstName( "Test" );
		created.setLastName( "User 2" );
		created.setDisplayName( "Display name for test user" );

		created = userService.save( created );

		found = userService.findUsers( user.email.eq( "some@email.com" ), new QPageRequest( 0, 10 ) );
		assertEquals( 1, found.getTotalElements() );
		assertEquals( 1, found.getTotalPages() );
		assertEquals( created, found.getContent().get( 0 ) );

		found = userService.findUsers( user.lastName.startsWithIgnoreCase( "user" ), new QPageRequest( 0, 10 ) );
		assertTrue( found.getTotalElements() >= 1 );
		assertEquals( 1, found.getTotalPages() );
		assertTrue( found.getContent().contains( created ) );

		found = userService.findUsers( user.email.eq( "none@email.com" ), new QPageRequest( 0, 10 ) );
		assertEquals( 0, found.getTotalElements() );
		assertEquals( 0, found.getTotalPages() );
	}

	@Test
	public void usersInGroups() {
		Group groupOne = new Group();
		groupOne.setName( RandomStringUtils.randomAlphanumeric( 20 ) );
		groupOne = groupService.save( groupOne );

		Group groupTwo = new Group();
		groupTwo.setName( groupOne.getName() + "2" );
		groupTwo = groupService.save( groupTwo );

		Group groupThree = new Group();
		groupThree.setName( groupOne.getName() + "3" );
		groupThree = groupService.save( groupThree );

		User userInGroupOne = new User();
		userInGroupOne.setUsername( "groupOne@email.com" );
		userInGroupOne.setEmail( "groupOne@email.com" );
		userInGroupOne.setPassword( "test password" );
		userInGroupOne.setFirstName( "Test" );
		userInGroupOne.setLastName( "User 2" );
		userInGroupOne.setDisplayName( "Display name for test user" );
		userInGroupOne.addGroup( groupOne );

		userInGroupOne = userService.save( userInGroupOne );

		User userInBoth = new User();
		userInBoth.setUsername( "userInBoth@email.com" );
		userInBoth.setEmail( "userInBoth@email.com" );
		userInBoth.setPassword( "test password" );
		userInBoth.setFirstName( "Test" );
		userInBoth.setLastName( "User 2" );
		userInBoth.setDisplayName( "Display name for test user" );
		userInBoth.addGroup( groupOne );
		userInBoth.addGroup( groupTwo );

		userInBoth = userService.save( userInBoth );

		QUser user = QUser.user;

		Collection<User> found = userService.findUsers( user.groups.contains( groupOne ) );
		assertEquals( 2, found.size() );
		assertTrue( found.contains( userInGroupOne ) );
		assertTrue( found.contains( userInBoth ) );

		found = userService.findUsers( user.groups.contains( groupTwo ) );
		assertEquals( 1, found.size() );
		assertTrue( found.contains( userInBoth ) );

		found = userService.findUsers( user.groups.contains( groupThree ).and( user.groups.contains( groupOne ) ));
		assertTrue( found.isEmpty() );
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
		}

		private PropertiesModule propertiesModule() {
			return new PropertiesModule();
		}

		private UserModule userModule() {
			return new UserModule();
		}
	}
}
