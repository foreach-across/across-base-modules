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
package com.foreach.across.modules.entity.views.forms.elements.select;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.forms.elements.CommonFormElements;
import com.foreach.across.modules.entity.views.forms.elements.FormElementBuilderFactoryAssemblerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

/**
 * @author Arne Vandamme
 */
public class SelectFormElementBuilderFactoryAssembler
		extends FormElementBuilderFactoryAssemblerSupport<SelectFormElementBuilder>
{
	@Autowired
	private EntityRegistry entityRegistry;

	public SelectFormElementBuilderFactoryAssembler() {
		super( SelectFormElementBuilder.class, CommonFormElements.SELECT );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void assembleTemplate( EntityConfiguration entityConfiguration,
	                                 EntityPropertyRegistry registry,
	                                 EntityPropertyDescriptor descriptor,
	                                 SelectFormElementBuilder template ) {
		Class<?> propertyType = descriptor.getPropertyType();

		if ( propertyType.isEnum() ) {
			template.setOptionGenerator( new EnumSelectOptionGenerator( (Class<? extends Enum>) propertyType ) );
		}
		else {
			EntityConfiguration member = entityRegistry.getEntityConfiguration( propertyType );

			if ( member != null ) {
				Repository repository = member.getAttribute( Repository.class );

				if ( repository != null && repository instanceof CrudRepository ) {
					template.setOptionGenerator(
							new EntityCrudRepositoryOptionGenerator( member, (CrudRepository) repository )
					);
				}
			}
		}
	}
}
