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
package com.foreach.across.modules.entity.views.elements;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.CompanyStatus;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestCommonViewElementTypeLookupStrategy.Config.class, loader = MockedLoader.class)
public class TestCommonViewElementTypeLookupStrategy
{
	@Autowired
	private CommonViewElementTypeLookupStrategy strategy;

	@Autowired
	private EntityRegistry entityRegistry;

	private EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
	private EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );

	@Before
	public void resetMocks() {
		reset( entityConfiguration, descriptor );

		when( descriptor.isWritable() ).thenReturn( true );
	}

	@Test
	public void hiddenTypeShouldNeverBeDisplayed() {
		when( descriptor.isHidden() ).thenReturn( true );
		assertNull( lookup( ViewElementMode.FOR_WRITING ) );
		assertNull( lookup( ViewElementMode.FOR_READING ) );
	}

	@Test
	public void textboxTypeForPrimitives() {
		assertEquals( CommonViewElements.TEXTBOX, lookup( String.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXTBOX, lookup( Integer.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXTBOX, lookup( int.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXTBOX, lookup( AtomicInteger.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXTBOX, lookup( Long.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXTBOX, lookup( BigDecimal.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, lookup( String.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( Integer.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( int.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( AtomicInteger.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( Long.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( BigDecimal.class, ViewElementMode.FOR_READING ) );
	}

	@Test
	public void dateTypeForDates() throws Exception {
		assertEquals( CommonViewElements.DATE, lookup( Date.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, lookup( Date.class, ViewElementMode.FOR_READING ) );
	}

	@Test
	public void checkboxTypeForBooleans() {
		assertEquals( CommonViewElements.CHECKBOX, lookup( Boolean.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.CHECKBOX, lookup( boolean.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.CHECKBOX, lookup( AtomicBoolean.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, lookup( Boolean.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( boolean.class, ViewElementMode.FOR_READING ) );
		assertEquals( CommonViewElements.TEXT, lookup( AtomicBoolean.class, ViewElementMode.FOR_READING ) );
	}

	@Test
	public void enumValueShouldReturnSelectType() {
		assertEquals( CommonViewElements.SELECT, lookup( CompanyStatus.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, lookup( CompanyStatus.class, ViewElementMode.FOR_READING ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void singleEntityTypeShouldReturnSelectType() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );

		when( entityConfiguration.getEntityType() ).thenReturn( (Class) Client.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );

		assertEquals( CommonViewElements.SELECT, lookup( Client.class, ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, lookup( Client.class, ViewElementMode.FOR_READING ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectionEntityTypeShouldReturnMultiCheckbox() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );

		when( entityConfiguration.getEntityType() ).thenReturn( (Class) Client.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );

		when( descriptor.getPropertyType() ).thenReturn( (Class) List.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection( List.class, TypeDescriptor.valueOf(
				Client.class ) );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );
//		when( descriptor.getPropertyTypeDescriptor().getResolvableType() )
//				.thenReturn( ResolvableType.forClassWithGenerics( List.class, Client.class ) );

		assertEquals( CommonViewElements.MULTI_CHECKBOX, strategy.findElementType( entityConfiguration, descriptor,
		                                                                           ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, strategy.findElementType( entityConfiguration, descriptor,
		                                                                 ViewElementMode.FOR_READING ) );
	}

	@Test
	public void unknownType() {
		assertNull( lookup( ViewElementMode.FOR_WRITING ) );
		assertEquals( CommonViewElements.TEXT, lookup( ViewElementMode.FOR_READING ) );
	}

	@SuppressWarnings("unchecked")
	private String lookup( Class propertyType, ViewElementMode mode ) {
		when( descriptor.getPropertyType() ).thenReturn( propertyType );
		return strategy.findElementType( entityConfiguration, descriptor, mode );
	}

	private String lookup( ViewElementMode mode ) {
		return strategy.findElementType( entityConfiguration, descriptor, mode );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public CommonViewElementTypeLookupStrategy lookupStrategy() {
			return new CommonViewElementTypeLookupStrategy();
		}
	}
}
