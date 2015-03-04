package com.foreach.across.modules.it.user;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.query.EntityQueryPageFetcher;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.QUser;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This test verifies that QueryDSL is used by default for building Entity queries.  Favour QueryDSL
 * instead of JPA due to Hibernate integration bugs: see https://hibernate.atlassian.net/browse/HHH-5948.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITUserModule.Config.class, ITUserAndGroupService.Config.class })
public class ITUserAndGroupService
{
	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	@SuppressWarnings( "unchecked" )
	public void entityQueryPageFetcher() {
		Group group = createTestUserAndGroup();

		Collection<User> users = userService.findUsers( QUser.user.groups.contains( group ) );
		assertEquals( 1, users.size() );
		assertEquals( Long.valueOf( -9875L ), users.iterator().next().getId() );

		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Group.class );
		EntityAssociation association = entityConfiguration.association( "user.groups" );
		assertNotNull( association );

		EntityQueryPageFetcher queryPageFetcher = association.getTargetEntityConfiguration()
		                                                     .getAttribute( EntityQueryPageFetcher.class );
		assertNotNull( queryPageFetcher );

		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "groups", EntityQueryOps.CONTAINS, group )
		);

		Page page = queryPageFetcher.fetchPage( query, null );

		users = (Collection<User>) page.getContent();
		assertEquals( 1, users.size() );
		assertEquals( Long.valueOf( -9875L ), users.iterator().next().getId() );
	}

	private Group createTestUserAndGroup() {
		Group group = new Group();
		group.setNewEntityId( -9876L );
		group.setName( "Test group" );

		groupService.save( group );

		User user = new User();
		user.setNewEntityId( -9875L );
		user.setUsername( "uag" );
		user.setEmail( "uag@localhost" );
		user.setFirstName( "Test" );
		user.setLastName( "User" );

		user.setPassword( "pwd" );
		user.addGroup( group );

		userService.save( user );

		return group;
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new EntityModule() );
		}
	}
}
