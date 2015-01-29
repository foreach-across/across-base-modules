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
package com.foreach.across.modules.entity.views.forms.elements;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.forms.CloningFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.forms.FormElementBuilderFactory;
import com.foreach.across.modules.entity.views.forms.FormElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.support.ConversionServiceConvertingValuePrinter;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
public abstract class FormElementBuilderFactoryAssemblerSupport<T extends FormElementBuilderSupport>
		implements FormElementBuilderFactoryAssembler
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
	public boolean supports( String formElementType ) {
		return supportedTypes.contains( formElementType );
	}

	@Override
	public FormElementBuilderFactory createBuilderFactory( EntityConfiguration entityConfiguration,
	                                                       EntityPropertyRegistry propertyRegistry,
	                                                       EntityPropertyDescriptor descriptor ) {
		T template
				= createTemplate( entityConfiguration, propertyRegistry, descriptor );

		CloningFormElementBuilderFactory<T> builderFactory = new CloningFormElementBuilderFactory<>( builderClass );
		builderFactory.setBuilderTemplate( template );

		return builderFactory;
	}

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

		assembleTemplate( entityConfiguration, registry, descriptor, template );

		handleConstraints( descriptor, template );

		return template;
	}

	protected void handleConstraints( EntityPropertyDescriptor descriptor, T template ) {
		PropertyDescriptor validationDescriptor = descriptor.getAttribute( PropertyDescriptor.class );

		if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
			for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
				Class<? extends Annotation> type = constraint.getAnnotation().annotationType();

				if ( NotBlank.class.equals( type ) || NotNull.class.equals( type ) || NotEmpty.class.equals( type ) ) {
					template.setRequired( true );
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
		return new ConversionServiceConvertingValuePrinter<>( valueFetcher, conversionService );
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

}
