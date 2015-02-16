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
package com.foreach.across.modules.entity.views.elements.form.select;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.CompanyStatus;
import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderFactoryAssemblerSupport;
import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderFactoryAssemblerTestSupport;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestSelectFormElementBuilderFactoryAssembler.Config.class, loader = MockedLoader.class)
public class TestSelectFormElementBuilderFactoryAssembler
		extends FormElementBuilderFactoryAssemblerTestSupport<SelectFormElementBuilder>
{
	@Autowired
	private SelectFormElementBuilderFactoryAssembler assembler;

	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Override
	protected FormElementBuilderFactoryAssemblerSupport getAssembler() {
		return assembler;
	}

	@Test
	public void createWithoutValidators() {
		template = assembleAndVerify( "noValidator" );
		assertFalse( template.isRequired() );
		assertTrue( template.getOptionGenerator() instanceof EnumSelectOptionGenerator );
	}

	@Test
	public void notNullValidator() {
		template = assembleAndVerify( "notNullValidator" );
		assertTrue( template.isRequired() );
		assertTrue( template.getOptionGenerator() instanceof EnumSelectOptionGenerator );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void clientProperty() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );
		when( clientConfig.getAttribute( Repository.class ) ).thenReturn( mock( CrudRepository.class ) );

		template = assembleAndVerify( "client" );
		assertNotNull( template.getOptionGenerator() );
		assertEquals( CommonViewElements.SELECT, template.getElementType() );
		assertTrue( template.getOptionGenerator() instanceof EntityCrudRepositoryOptionGenerator );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void othersProperty() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );
		when( clientConfig.getAttribute( Repository.class ) ).thenReturn( mock( CrudRepository.class ) );

		template = assembleAndVerify( "others" );
		assertNotNull( template.getOptionGenerator() );
		assertEquals( CommonViewElements.MULTI_CHECKBOX, template.getElementType() );
		assertTrue( template.getOptionGenerator() instanceof EntityCrudRepositoryOptionGenerator );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public SelectFormElementBuilderFactoryAssembler selectFormElementBuilderFactoryAssembler() {
			return new SelectFormElementBuilderFactoryAssembler();
		}
	}

	private static class Validators
	{
		public CompanyStatus noValidator;

		@NotNull
		public CompanyStatus notNullValidator;

		public Client client;

		public Set<Client> others;
	}
}
