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

import com.foreach.across.modules.entity.EntityAttributes;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class FieldsetViewElementBuilderFactoryAssembler implements ViewElementBuilderFactoryAssembler
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
		FieldsetViewElementBuilder template
				= createTemplate( entityConfiguration, propertyRegistry, descriptor );

		CloningViewElementBuilderFactory<FieldsetViewElementBuilder> builderFactory
				= new CloningViewElementBuilderFactory<>( FieldsetViewElementBuilder.class );
		builderFactory.setBuilderTemplate( template );

		return builderFactory;
	}

	@SuppressWarnings( "unchecked" )
	protected FieldsetViewElementBuilder createTemplate(
			EntityConfiguration entityConfiguration,
			EntityPropertyRegistry registry,
			EntityPropertyDescriptor descriptor
	) {
		FieldsetViewElementBuilder template = new FieldsetViewElementBuilder();
		template.setMessageCodeResolver( entityConfiguration.getEntityMessageCodeResolver() );
		template.setName( descriptor.getName() );
		template.setLabel( descriptor.getDisplayName() );
		template.setLabelCode( "properties." + descriptor.getName() );
		template.setValuePrinter( createValuePrinter( descriptor ) );

		// Set the properties
		PropertyPersistenceMetadata metadata = descriptor.getAttribute( EntityAttributes.PROPERTY_PERSISTENCE_METADATA,
		                                                                PropertyPersistenceMetadata.class );

		List<String> properties = new ArrayList<>();

		if ( metadata != null && metadata.isEmbedded() ) {
			EntityPropertyRegistry subRegistry = entityPropertyRegistries.getRegistry( descriptor.getPropertyType() );

			for ( EntityPropertyDescriptor subDescriptor : subRegistry.getProperties() ) {
				if ( subDescriptor.isWritable() ) {
					properties.add( descriptor.getName() + "." + subDescriptor.getName() );
				}
			}
		}
		else if ( descriptor.hasAttribute( EntityAttributes.PROPERTY_GROUP ) ) {
			Collection<String> memberNamesInOrder = descriptor.getAttribute( EntityAttributes.PROPERTY_GROUP );

			properties.addAll( memberNamesInOrder );
		}

		Map dependencies = descriptor.getAttribute("dependencies", Map.class);
		template.setDependencies(dependencies);

		template.setProperties( properties );

		return template;
	}

	protected ValuePrinter createValuePrinter( EntityPropertyDescriptor descriptor ) {
		// todo: has existing valueprinter, has existing printer (?)
		ValueFetcher<?> valueFetcher = descriptor.getValueFetcher();
		return new ConversionServiceConvertingValuePrinter<>( valueFetcher, conversionService );
	}
}
