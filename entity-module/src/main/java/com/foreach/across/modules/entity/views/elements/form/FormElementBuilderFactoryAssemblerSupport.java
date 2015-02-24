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
package com.foreach.across.modules.entity.views.elements.form;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.elements.CloningViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.support.ConversionServiceConvertingValuePrinter;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
public abstract class FormElementBuilderFactoryAssemblerSupport<T extends FormElementBuilderSupport>
		implements ViewElementBuilderFactoryAssembler
{
	private final Class<T> builderClass;
	private final Set<String> supportedTypes = new HashSet<>();

	@Autowired
	private ConversionService conversionService;

	protected FormElementBuilderFactoryAssemblerSupport( Class<T> builderClass, String... elementTypes ) {
		this.builderClass = builderClass;
		supportedTypes.addAll( Arrays.asList( elementTypes ) );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return supportedTypes.contains( viewElementType );
	}

	@Override
	public ViewElementBuilderFactory createBuilderFactory( EntityConfiguration entityConfiguration,
	                                                       EntityPropertyRegistry propertyRegistry,
	                                                       EntityPropertyDescriptor descriptor ) {
		T template
				= createTemplate( entityConfiguration, propertyRegistry, descriptor );

		CloningViewElementBuilderFactory<T> builderFactory = new CloningViewElementBuilderFactory<>( builderClass );
		builderFactory.setBuilderTemplate( template );

		return builderFactory;
	}

	@SuppressWarnings("unchecked")
	protected T createTemplate(
			EntityConfiguration entityConfiguration,
			EntityPropertyRegistry registry,
			EntityPropertyDescriptor descriptor
	) {
		T template = newInstance();
		template.setMessageCodeResolver( entityConfiguration.getEntityMessageCodeResolver() );
		template.setName( descriptor.getName() );
		template.setLabel( descriptor.getDisplayName() );
		template.setLabelCode( "properties." + descriptor.getName() );
		template.setValuePrinter( createValuePrinter( descriptor ) );

		// todo: only if *native* property
		template.setField( true );

		Map dependencies = descriptor.getAttribute( "dependencies", Map.class );
		template.setDependencies( dependencies );

		assembleTemplate( entityConfiguration, registry, descriptor, template );

		handleConstraints( descriptor, template );

		return template;
	}

	protected void handleConstraints( EntityPropertyDescriptor descriptor, T template ) {
		PropertyDescriptor validationDescriptor = descriptor.getAttribute( PropertyDescriptor.class );

		if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
			for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
				Annotation annotation = constraint.getAnnotation();
				Class<? extends Annotation> type = annotation.annotationType();

				if ( NotBlank.class.equals( type ) ) {
					NotBlank notBlank = (NotBlank) annotation;
					Class<?>[] groups = notBlank.groups();
					checkValidationGroups( template, groups );
				}
				else if ( NotNull.class.equals( type ) ) {
					NotNull notBlank = (NotNull) annotation;
					Class<?>[] groups = notBlank.groups();
					checkValidationGroups( template, groups );
				}
				else if ( NotEmpty.class.equals( type ) ) {
					NotEmpty notBlank = (NotEmpty) annotation;
					Class<?>[] groups = notBlank.groups();
					checkValidationGroups( template, groups );
				}
				else {
					handleConstraint( template, type, constraint );
				}
			}
		}
	}

	protected void handleConstraint( T template, Class<? extends Annotation> type, ConstraintDescriptor constraint ) {

	}

	/**
	 * Simple implementations should override this method to extend the base template already created.
	 */
	protected void assembleTemplate( EntityConfiguration entityConfiguration,
	                                 EntityPropertyRegistry registry,
	                                 EntityPropertyDescriptor descriptor,
	                                 T template ) {

	}

	protected ValuePrinter createValuePrinter( EntityPropertyDescriptor descriptor ) {
		// todo: has existing valueprinter, has existing printer (?)
		ValueFetcher<?> valueFetcher = descriptor.getValueFetcher();
		TypeDescriptor propertyTypeDescriptor = descriptor.getPropertyTypeDescriptor();
		return new ConversionServiceConvertingValuePrinter<>( valueFetcher, propertyTypeDescriptor, conversionService );
	}

	protected T newInstance() {
		try {
			return builderClass.newInstance();
		}
		catch ( IllegalAccessException | InstantiationException iae ) {
			throw new RuntimeException(
					getClass().getSimpleName() + " requires the template to have a parameterless constructor", iae
			);
		}
	}

	private void checkValidationGroups( T template, Class<?>[] groups ) {
		if ( groups.length == 0 || ( groups.length == 1 && Default.class.isAssignableFrom( groups[0] ) ) ) {
			template.setRequired( true );
		}
	}

}
