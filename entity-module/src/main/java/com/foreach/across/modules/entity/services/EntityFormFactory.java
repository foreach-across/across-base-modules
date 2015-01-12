package com.foreach.across.modules.entity.services;

import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.business.FormPropertyDescriptor;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.form.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Service
public class EntityFormFactory
{
	@Autowired
	private EntityRegistryImpl entityRegistry;

	public EntityForm create( Collection<FormPropertyDescriptor> descriptors ) {
		EntityForm form = new EntityForm();

		for ( FormPropertyDescriptor descriptor : descriptors ) {
			descriptor.setDisplayName( StringUtils.capitalize( descriptor.getName() ) );

			if ( descriptor.isReadable() && descriptor.isWritable() ) {
				if ( descriptor.getPropertyType().equals( boolean.class )
						|| descriptor.getPropertyType().equals( Boolean.class ) ) {
					form.addElement( new CheckboxFormElement( descriptor ) );
				}
				else if ( Collection.class.isAssignableFrom( descriptor.getPropertyType() ) ) {
					ResolvableType type = descriptor.getPropertyResolvableType();

					if ( type.hasGenerics() ) {
						Class itemType = type.getGeneric( 0 ).resolve();

						Collection<?> possibleValues = Collections.emptyList();

						if ( itemType.isEnum() ) {
							possibleValues = Arrays.asList( itemType.getEnumConstants() );
						}
						else {
							EntityConfiguration itemEntityType = entityRegistry.getEntityByClass( itemType );

							if ( itemEntityType != null ) {
								possibleValues = itemEntityType.getRepository().findAll();
							}
						}

						form.addElement( new MultiCheckboxFormElement( entityRegistry, descriptor, possibleValues ) );

					}
				}
				else if ( entityRegistry.getEntityByClass( descriptor.getPropertyType() ) != null ) {
					EntityConfiguration itemEntityType = entityRegistry.getEntityByClass(
							descriptor.getPropertyType() );

					form.addElement(
							new SelectFormElement( entityRegistry, descriptor, itemEntityType.getRepository().findAll() )
					);
				}
				else {
					form.addElement( new TextboxFormElement( descriptor ) );
				}
			}
		}

		return form;
	}

	public EntityForm create( EntityConfiguration entityConfiguration ) {
		Class entityClass = entityConfiguration.getEntityType();

		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors( entityClass );

		EntityForm form = new EntityForm();

		for ( PropertyDescriptor descriptor : descriptors ) {
			descriptor.setDisplayName( StringUtils.capitalize( descriptor.getName() ) );

			if ( descriptor.getWriteMethod() != null && descriptor.getReadMethod() != null ) {
				if ( descriptor.getName().equals( "id" ) ) {
					form.addElement( new HiddenFormElement( descriptor ) );
				}
				else if ( descriptor.getPropertyType().equals( boolean.class ) || descriptor.getPropertyType().equals(
						Boolean.class ) ) {
					form.addElement( new CheckboxFormElement( descriptor ) );
				}
				else if ( Collection.class.isAssignableFrom( descriptor.getPropertyType() ) ) {
					ResolvableType type = ResolvableType.forMethodParameter( descriptor.getWriteMethod(), 0 );

					if ( type.hasGenerics() ) {
						Class itemType = type.getGeneric( 0 ).resolve();

						Collection<?> possibleValues = Collections.emptyList();

						if ( itemType.isEnum() ) {
							possibleValues = Arrays.asList( itemType.getEnumConstants() );
						}
						else {
							EntityConfiguration itemEntityType = entityRegistry.getEntityByClass( itemType );

							if ( itemEntityType != null ) {
								possibleValues = itemEntityType.getRepository().findAll();
							}
						}

						form.addElement( new MultiCheckboxFormElement( entityRegistry, descriptor, possibleValues ) );

					}
				}
				else if ( entityRegistry.getEntityByClass( descriptor.getPropertyType() ) != null ) {
					EntityConfiguration itemEntityType = entityRegistry.getEntityByClass(
							descriptor.getPropertyType() );

					form.addElement(
							new SelectFormElement( entityRegistry, descriptor, itemEntityType.getRepository().findAll() )
					);
				}
				else {
					form.addElement( new TextboxFormElement( descriptor ) );
				}
			}
		}

		return form;
	}
}
