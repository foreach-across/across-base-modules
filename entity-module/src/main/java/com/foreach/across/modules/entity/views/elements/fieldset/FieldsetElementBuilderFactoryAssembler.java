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
package com.foreach.across.modules.entity.views.elements.fieldset;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistries;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.views.elements.CloningViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.support.ConversionServiceConvertingValuePrinter;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class FieldsetElementBuilderFactoryAssembler implements ViewElementBuilderFactoryAssembler
{
	@Autowired
	private ConversionService conversionService;

	@Autowired
	private EntityPropertyRegistries entityPropertyRegistries;

	@Override
	public boolean supports( String viewElementType ) {
		return CommonViewElements.FIELDSET.equals( viewElementType );
	}

	@Override
	public ViewElementBuilderFactory createBuilderFactory( EntityConfiguration entityConfiguration,
	                                                       EntityPropertyRegistry propertyRegistry,
	                                                       EntityPropertyDescriptor descriptor ) {
		FieldsetElementBuilder template
				= createTemplate( entityConfiguration, propertyRegistry, descriptor );

		CloningViewElementBuilderFactory<FieldsetElementBuilder> builderFactory
				= new CloningViewElementBuilderFactory<>( FieldsetElementBuilder.class );
		builderFactory.setBuilderTemplate( template );

		return builderFactory;
	}

	protected FieldsetElementBuilder createTemplate(
			EntityConfiguration entityConfiguration,
			EntityPropertyRegistry registry,
			EntityPropertyDescriptor descriptor
	) {
		FieldsetElementBuilder template = new FieldsetElementBuilder();
		template.setMessageCodeResolver( entityConfiguration.getEntityMessageCodeResolver() );
		template.setName( descriptor.getName() );
		template.setLabel( descriptor.getDisplayName() );
		template.setLabelCode( "properties." + descriptor.getName() );
		template.setValuePrinter( createValuePrinter( descriptor ) );

		// Set the properties
		PropertyPersistenceMetadata metadata = descriptor.getAttribute( PropertyPersistenceMetadata.class );

		List<String> properties = new ArrayList<>();

		if ( metadata != null && metadata.isEmbedded() ) {
			EntityPropertyRegistry subRegistry = entityPropertyRegistries.getRegistry( descriptor.getPropertyType() );

			for ( EntityPropertyDescriptor subDescriptor : subRegistry.getProperties() ) {
				properties.add( descriptor.getName() + "."+ subDescriptor.getName() );
			}
		}

		template.setProperties( properties );

		return template;
	}

	protected ValuePrinter createValuePrinter( EntityPropertyDescriptor descriptor ) {
		// todo: has existing valueprinter, has existing printer (?)
		ValueFetcher<?> valueFetcher = descriptor.getValueFetcher();
		return new ConversionServiceConvertingValuePrinter<>( valueFetcher, conversionService );
	}
}
