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
package com.foreach.across.modules.hibernate;

import com.foreach.across.modules.hibernate.testmodules.hibernate1.Product;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.ProductRepository;
import com.foreach.across.modules.hibernate.testmodules.hibernate2.User;
import com.foreach.across.modules.hibernate.testmodules.hibernate2.UserRepository;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestUnitOfWorkInSameThread.Config.class)
@DirtiesContext
public class TestUnitOfWorkInThread
{
	@Autowired
	private UnitOfWorkFactory unitOfWork;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	private Exception thrown;

	@Before
	public void resetException() {
		thrown = null;
	}

	@Test
	public void singleModuleTransactional() throws Exception {
		execute( new Runnable()
		{
			public void run() {
				try {
					assertNull( productRepository.getProductWithId( 1 ) );

					Product product = new Product( 1, "product 1" );
					productRepository.save( product );

					Product other = productRepository.getProductWithId( 1 );
					assertNotNull( other );
					assertEquals( product, other );
				}
				catch ( Exception e ) {
					thrown = e;
				}
			}
		} );
	}

	@Test
	public void otherModuleTransactional() throws Exception {
		execute( new Runnable()
		{
			public void run() {
				try {
					assertNull( userRepository.getUserWithId( 1 ) );

					User user = new User( 1, "user 1" );
					userRepository.save( user );

					unitOfWork.restart();

					User other = userRepository.getUserWithId( 1 );
					assertNotNull( other );
					assertEquals( user, other );
				}
				catch ( Exception e ) {
					thrown = e;
				}
			}
		} );
	}

	@Test
	public void combinedSave() throws Exception {
		execute( new Runnable()
		{
			public void run() {
				try {
					Product product = new Product( 2, "product 2" );
					User user = new User( 2, "user 2" );

					userRepository.save( user, product );

					unitOfWork.restart();

					User otherUser = userRepository.getUserWithId( 2 );
					assertNotNull( otherUser );
					assertEquals( user, otherUser );

					Product otherProduct = productRepository.getProductWithId( 2 );
					assertNotNull( otherProduct );
					assertEquals( product, otherProduct );
				}
				catch ( Exception e ) {
					thrown = e;
				}
			}
		} );
	}

	@Test
	public void combinedRollback() throws Exception {
		execute( new Runnable()
		{
			public void run() {
				try {
					Product product = new Product( 3, "product 3" );

					boolean failed = false;

					try {
						userRepository.save( null, product );
					}
					catch ( Exception e ) {
						failed = true;
					}

					assertTrue( failed );

					Product otherProduct = productRepository.getProductWithId( 3 );
					assertNull( otherProduct );
				}
				catch ( Exception e ) {
					thrown = e;
				}
			}
		} );
	}

	private void execute( Runnable runnable ) throws Exception {
		Thread t = new Thread( unitOfWork.create( runnable ) );
		t.start();
		t.join();

		if ( thrown != null ) {
			throw thrown;
		}
	}
}



