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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderFactoryAssemblerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public class SelectFormElementBuilderFactoryAssembler
		extends FormElementBuilderFactoryAssemblerSupport<SelectFormElementBuilder>
{
	@Autowired
	private EntityRegistry entityRegistry;

	public SelectFormElementBuilderFactoryAssembler() {
		super( SelectFormElementBuilder.class, CommonViewElements.SELECT, CommonViewElements.MULTI_CHECKBOX,
		       CommonViewElements.RADIO );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void assembleTemplate( EntityConfiguration entityConfiguration,
	                                 EntityPropertyRegistry registry,
	                                 EntityPropertyDescriptor descriptor,
	                                 SelectFormElementBuilder template ) {
		boolean isCollection = isCollection( descriptor );
		Class<?> memberType = isCollection ? determineCollectionMemberType( descriptor ) : descriptor.getPropertyType();

		if ( memberType == null ) {
			throw new RuntimeException( "Unable to determine property type specific enough for form element assembly "
					                            + descriptor.getName() );
		}

		SelectOptionGenerator selectOptionGenerator = descriptor.getAttribute( SelectOptionGenerator.class );
		if ( selectOptionGenerator != null ) {
			template.setOptionGenerator( selectOptionGenerator );
		}
		else if ( memberType.isEnum() ) {
			template.setOptionGenerator( new EnumSelectOptionGenerator( (Class<? extends Enum>) memberType ) );
		}
		else {
			EntityConfiguration optionType = entityRegistry.getEntityConfiguration( memberType );

			if ( optionType != null ) {
				Repository repository = optionType.getAttribute( Repository.class );

				if ( repository != null && repository instanceof CrudRepository ) {
					template.setOptionGenerator(
							new EntityCrudRepositoryOptionGenerator( optionType, (CrudRepository) repository )
					);
				}
			}
		}

		if ( isCollection ) {
			template.setElementType( CommonViewElements.MULTI_CHECKBOX );
		}
		else if ( CommonViewElements.RADIO.equals( descriptor.getAttribute(
				EntityAttributes.ELEMENT_TYPE_WRITABLE ) ) ) {
			template.setElementType( CommonViewElements.RADIO );
		}

	}

	private boolean isCollection( EntityPropertyDescriptor descriptor ) {
		return descriptor.getPropertyType().isArray() || Collection.class.isAssignableFrom(
				descriptor.getPropertyType() );
	}

	private Class determineCollectionMemberType( EntityPropertyDescriptor descriptor ) {
		if ( descriptor.getPropertyType().isArray() ) {
			return descriptor.getPropertyType().getComponentType();
		}

		ResolvableType resolvableType = descriptor.getPropertyTypeDescriptor().getResolvableType();

		if ( resolvableType != null && resolvableType.hasGenerics() ) {
			return resolvableType.resolveGeneric( 0 );
		}

		return null;
	}
}
