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
package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestUser
{
	@Test
	public void testUserDtoAcceptsNullRestrictionsAndCollections() {
		User dto = new User();
		dto.setRestrictions( null );
		assertEquals( Collections.<UserRestriction>emptySet(), dto.getRestrictions() );

		User dto2 = new User();
		Set<UserRestriction> restrictions = EnumSet.of( UserRestriction.REQUIRES_CONFIRMATION, UserRestriction.LOCKED,
		                                                UserRestriction.DISABLED );
		dto2.setRestrictions( restrictions );

		Set<UserRestriction> retrievedUserRestrictions = dto2.getRestrictions();
		assertNotNull( "restriction cannot be null", retrievedUserRestrictions );
		assertEquals( 3, restrictions.size() );
		assertEquals( restrictions, dto2.getRestrictions() );

		assertFalse( dto.hasRestriction( null ) );
		assertTrue( dto2.hasRestriction( UserRestriction.LOCKED ) );

		User dto3 = new User();
		dto3.setRestrictions( new HashSet<UserRestriction>() );

		Set<UserRestriction> retrievedUserRestrictions3 = dto3.getRestrictions();
		assertNotNull( "restriction cannot be null", retrievedUserRestrictions3 );
		assertEquals( 0, retrievedUserRestrictions3.size() );
		assertEquals( Collections.<UserRestriction>emptySet(), retrievedUserRestrictions3 );
	}
}
