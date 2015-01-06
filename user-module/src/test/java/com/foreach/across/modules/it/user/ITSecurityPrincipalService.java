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

import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITUserModule.Config.class })
public class ITSecurityPrincipalService
{
	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Test
	public void userPrincipals() {
		User expectedUserOne = createUser( UUID.randomUUID().toString() );
		User expectedUserTwo = createUser( UUID.randomUUID().toString() );

		assertEquals( expectedUserOne,
		              securityPrincipalService.getPrincipalByName( expectedUserOne.getPrincipalName() ) );
		assertEquals( expectedUserTwo,
		              securityPrincipalService.getPrincipalByName( expectedUserTwo.getPrincipalName() ) );

		UserProperties propsOne = userService.getProperties( expectedUserOne );
		propsOne.put( "custom.property", 777L );
		propsOne.put( "other.property", 19 );
		userService.saveProperties( propsOne );

		UserProperties propsTwo = userService.getProperties( expectedUserTwo );
		propsTwo.put( "custom.property", 888L );
		propsTwo.put( "other.property", 19 );
		userService.saveProperties( propsTwo );

		Collection<User> users = userService.getUsersWithPropertyValue( "custom.property", 888L );
		assertNotNull( users );
		assertEquals( 1, users.size() );
		assertTrue( users.contains( expectedUserTwo ) );

		users = userService.getUsersWithPropertyValue( "other.property", 19 );
		assertNotNull( users );
		assertEquals( 2, users.size() );
		assertTrue( users.contains( expectedUserOne ) );
		assertTrue( users.contains( expectedUserTwo ) );
	}

	@Test
	public void groupPrincipals() {
		Group expectedGroupOne = createGroup( RandomStringUtils.randomAscii( 50 ) );
		Group expectedGroupTwo = createGroup( RandomStringUtils.randomAscii( 50 ) );

		assertEquals( expectedGroupOne,
		              securityPrincipalService.getPrincipalByName( expectedGroupOne.getPrincipalName() ) );
		assertEquals( expectedGroupTwo,
		              securityPrincipalService.getPrincipalByName( expectedGroupTwo.getPrincipalName() ) );

		GroupProperties propsOne = groupService.getProperties( expectedGroupOne );
		propsOne.put( "custom.property", 777L );
		propsOne.put( "other.property", 19 );
		groupService.saveProperties( propsOne );

		GroupProperties propsTwo = groupService.getProperties( expectedGroupTwo );
		propsTwo.put( "custom.property", 888L );
		propsTwo.put( "other.property", 19 );
		groupService.saveProperties( propsTwo );

		Collection<Group> groups = groupService.getGroupsWithPropertyValue( "custom.property", 888L );
		assertNotNull( groups );
		assertEquals( 1, groups.size() );
		assertTrue( groups.contains( expectedGroupTwo ) );

		groups = groupService.getGroupsWithPropertyValue( "other.property", 19 );
		assertNotNull( groups );
		assertEquals( 2, groups.size() );
		assertTrue( groups.contains( expectedGroupOne ) );
		assertTrue( groups.contains( expectedGroupTwo ) );
	}

	private Group createGroup( String name ) {
		Group dto = new Group();
		dto.setName( name );

		return groupService.save( dto );
	}

	private User createUser( String username ) {
		User user = new User();
		user.setUsername( username );
		user.setEmail( UUID.randomUUID() + "@test.com" );
		user.setPassword( "test" );
		user.setFirstName( RandomStringUtils.randomAscii( 25 ) );
		user.setLastName( RandomStringUtils.randomAscii( 25 ) );
		user.setDisplayName( RandomStringUtils.randomAscii( 50 ) );

		return userService.save( user );
	}
}
