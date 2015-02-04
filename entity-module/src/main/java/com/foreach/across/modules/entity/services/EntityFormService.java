package com.foreach.across.modules.entity.services;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.business.FormPropertyDescriptor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.forms.*;
import com.foreach.across.modules.entity.views.properties.PrintablePropertyGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 *
 */
@Service
public class EntityFormService
{
	@RefreshableCollection(incremental = true, includeModuleInternals = true)
	private Collection<FormElementTypeLookupStrategy> elementTypeLookupStrategies;

	@RefreshableCollection(incremental = true, includeModuleInternals = true)
	private Collection<FormElementBuilderFactoryAssembler> builderFactoryAssemblers;

	public FormElement createFormElement( EntityConfiguration entityConfiguration,
	                                      EntityPropertyRegistry entityPropertyRegistry,
	                                      EntityPropertyDescriptor descriptor,
	                                      EntityMessageCodeResolver messageCodeResolver ) {
		FormElementBuilderFactory builderFactory
				= getOrCreateBuilderFactory( entityConfiguration, entityPropertyRegistry, descriptor );

		if ( builderFactory != null ) {
			FormElementBuilder builder = builderFactory.createBuilder();

			if ( builder != null ) {
				builder.setMessageCodeResolver( messageCodeResolver );

				// todo: remove test section
				if ( StringUtils.equals( "name", descriptor.getName() ) ) {
					PrintablePropertyGroup group = new PrintablePropertyGroup();
					group.setName( "name" );    // name must be a valid property on the entity, identifiable by beanwrapper
					group.setLabel( "Name group" );
					group.getChildren().add( builder.createFormElement() );

					return group;
				}

				return builder.createFormElement();
			}
		}

		return null;
	}

	public FormElementBuilderFactory getOrCreateBuilderFactory(
			EntityConfiguration entityConfiguration,
			EntityPropertyRegistry entityPropertyRegistry,
			EntityPropertyDescriptor propertyDescriptor
	) {
		// Get builder factory
		FormElementBuilderFactory builderFactory = propertyDescriptor.getAttribute( FormElementBuilderFactory.class );

		if ( builderFactory == null ) {
			builderFactory = createBuilderFactory( entityConfiguration, entityPropertyRegistry, propertyDescriptor );
		}

		return builderFactory;
	}

	public FormElementBuilderFactory createBuilderFactory(
			EntityConfiguration entityConfiguration,
			EntityPropertyRegistry entityPropertyRegistry,
			EntityPropertyDescriptor propertyDescriptor
	) {
		String elementType = findElementType( entityConfiguration, propertyDescriptor );

		if ( elementType == null ) {
			return null;
		}

		FormElementBuilderFactoryAssembler builderFactoryAssembler = findAssemblerForType( elementType );

		if ( builderFactoryAssembler == null ) {
			return null;
		}

		return builderFactoryAssembler
				.createBuilderFactory( entityConfiguration, entityPropertyRegistry, propertyDescriptor );
	}

	private String findElementType( EntityConfiguration entityConfiguration, EntityPropertyDescriptor descriptor ) {
		for ( FormElementTypeLookupStrategy lookupStrategy : elementTypeLookupStrategies ) {
			String elementType = lookupStrategy.findElementType( entityConfiguration, descriptor );
			if ( elementType != null ) {
				return elementType;
			}
		}

		return null;
	}

	private FormElementBuilderFactoryAssembler findAssemblerForType( String elementType ) {
		for ( FormElementBuilderFactoryAssembler assembler : builderFactoryAssemblers ) {
			if ( assembler.supports( elementType ) ) {
				return assembler;
			}
		}

		return null;
	}

	@Deprecated
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
