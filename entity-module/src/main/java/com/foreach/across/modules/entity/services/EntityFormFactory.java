package com.foreach.across.modules.entity.services;

import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.business.FormElement;
import com.foreach.across.modules.entity.business.FormPropertyDescriptor;
import com.foreach.across.modules.entity.form.HiddenFormElement;
import com.foreach.across.modules.entity.form.TextFormElement;
import com.foreach.across.modules.entity.form.TextboxFormElement;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class EntityFormFactory
{
	@Autowired
	private EntityRegistryImpl entityRegistry;

	@Autowired(required=false)
	private ConversionService conversionService;

	public FormElement createFormElement( EntityPropertyDescriptor descriptor,
	                                      EntityMessageCodeResolver messageCodeResolver ) {
		if ( descriptor.isHidden() ) {
			return new HiddenFormElement( messageCodeResolver, conversionService, descriptor );
		}

		if ( descriptor.isWritable() ) {
			return new TextboxFormElement( messageCodeResolver, conversionService, descriptor );
		}
		else {
			return new TextFormElement( messageCodeResolver, conversionService, descriptor );
		}
	}

	public EntityForm create( Collection<FormPropertyDescriptor> descriptors ) {
		EntityForm form = new EntityForm();

//		for ( FormPropertyDescriptor descriptor : descriptors ) {
//			descriptor.setDisplayName( StringUtils.capitalize( descriptor.getName() ) );
//
//			if ( descriptor.isReadable() && descriptor.isWritable() ) {
//				if ( descriptor.getPropertyType().equals( boolean.class )
//						|| descriptor.getPropertyType().equals( Boolean.class ) ) {
//					form.addElement( new CheckboxFormElement( descriptor ) );
//				}
//				else if ( Collection.class.isAssignableFrom( descriptor.getPropertyType() ) ) {
//					ResolvableType type = descriptor.getPropertyResolvableType();
//
//					if ( type.hasGenerics() ) {
//						Class itemType = type.getGeneric( 0 ).resolve();
//
//						Collection<?> possibleValues = Collections.emptyList();
//
//						if ( itemType.isEnum() ) {
//							possibleValues = Arrays.asList( itemType.getEnumConstants() );
//						}
//						else {
//							EntityConfiguration itemEntityType = entityRegistry.getEntityByClass( itemType );
//
//							if ( itemEntityType != null ) {
//								possibleValues = itemEntityType.getRepository().findAll();
//							}
//						}
//
//						form.addElement( new MultiCheckboxFormElement( entityRegistry, descriptor, possibleValues ) );
//
//					}
//				}
//				else if ( entityRegistry.getEntityByClass( descriptor.getPropertyType() ) != null ) {
//					EntityConfiguration itemEntityType = entityRegistry.getEntityByClass(
//							descriptor.getPropertyType() );
//
//					form.addElement(
//							new SelectFormElement( entityRegistry, descriptor, itemEntityType.getRepository().findAll() )
//					);
//				}
//				else {
//					form.addElement( new TextboxFormElement( descriptor ) );
//				}
//			}
//		}

		return form;
	}

	public EntityForm create( EntityConfiguration entityConfiguration ) {
		Class entityType = entityConfiguration.getEntityType();

		List<EntityPropertyDescriptor> descriptors = entityConfiguration.getPropertyRegistry().getProperties();

		EntityForm form = new EntityForm();

//		for ( EntityPropertyDescriptor descriptor : descriptors ) {
//			if ( descriptor.isHidden() ) {
//				form.addElement( new HiddenFormElement( descriptor ) );
//			}
//			else if ( descriptor.isReadable() && !descriptor.isWritable() ) {
//				form.addElement( new TextFormElement( descriptor ) );
//			}
//			else if ( descriptor.isWritable() && descriptor.isReadable() ) {
//				if ( descriptor.getPropertyType().equals( boolean.class ) || descriptor.getPropertyType().equals(
//						Boolean.class ) ) {
//					form.addElement( new CheckboxFormElement( descriptor ) );
//				}
//				else if ( Collection.class.isAssignableFrom( descriptor.getPropertyType() ) ) {
//					/*ResolvableType type = ResolvableType.forMethodParameter( descriptor.getWriteMethod(), 0 );
//
//					if ( type.hasGenerics() ) {
//						Class itemType = type.getGeneric( 0 ).resolve();
//
//						Collection<?> possibleValues = Collections.emptyList();
//
//						if ( itemType.isEnum() ) {
//							possibleValues = Arrays.asList( itemType.getEnumConstants() );
//						}
//						else {
//							EntityConfiguration itemEntityType = entityRegistry.getEntityByClass( itemType );
//
//							if ( itemEntityType != null ) {
//								possibleValues = itemEntityType.getRepository().findAll();
//							}
//						}
//
//						form.addElement( new MultiCheckboxFormElement( entityRegistry, descriptor, possibleValues ) );
//
//					}*/
//				}
//				else if ( entityRegistry.contains( descriptor.getPropertyType() ) ) {
//					/*EntityConfiguration itemEntityType = entityRegistry.getEntityByClass(
//							descriptor.getPropertyType() );
//
//					form.addElement(
//							new SelectFormElement( entityRegistry, descriptor, itemEntityType.getRepository().findAll() )
//					);*/
//				}
//				else {
//					form.addElement( new TextboxFormElement( descriptor ) );
//				}
//			}
//		}

		return form;
	}
}
