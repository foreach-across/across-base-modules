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
package com.foreach.across.modules.properties.controllers;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.business.EntityWrapper;
import com.foreach.across.modules.entity.business.FormPropertyDescriptor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.services.EntityFormService;
import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.services.EntityPropertiesService;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collection;

// todo: current implementation is not uptodate
@AdminWebController
@RequestMapping("/entities/{entityConfig}/{entityId}")
public class EntityPropertiesController
{
	@RefreshableCollection(includeModuleInternals = true)
	private Collection<EntityPropertiesDescriptor> propertiesDescriptors;

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private EntityRegistryImpl entityRegistry;

	@Autowired
	private EntityFormService formFactory;

	@Autowired
	private MenuFactory menuFactory;

	/*@Event
	protected void registerCustomPropertiesTab( EntityAdminMenuEvent<IdBasedEntity> menu ) {
		if ( menu.isForUpdate() && hasProperties( menu.getEntityType() ) ) {
			menu.builder().item( "properties",
			                     "Properties",
			                     "/entities/" + ( menu.getEntityType().getSimpleName().toLowerCase() ) + "/"
					                     + menu.getEntity().getId()
					                     + "/properties" )
			    .order( Ordered.HIGHEST_PRECEDENCE + 1 );
		}
	}*/

	private boolean hasProperties( Class<?> entityClass ) {
		return getDescriptor( entityClass ) != null;
	}

	@SuppressWarnings("unchecked")
	private EntityProperties loadProperties( Class<?> entityClass, Object entityId ) {
		EntityPropertiesDescriptor descriptor = getDescriptor( entityClass );

		if ( descriptor.service() instanceof EntityPropertiesService ) {
			EntityPropertiesService propertiesService = (EntityPropertiesService) descriptor.service();

			return propertiesService.getProperties( entityId );
		}
		else {
			throw new RuntimeException( "Revision based properties are currently unsupported" );
		}
	}

	private EntityPropertiesDescriptor getDescriptor( Class<?> entityClass ) {
		for ( EntityPropertiesDescriptor descriptor : propertiesDescriptors ) {
			if ( descriptor.entityClass() != null && descriptor.entityClass().isAssignableFrom( entityClass ) ) {
				return descriptor;
			}
		}

		return null;
	}

	@ModelAttribute("entity")
	public EntityWrapper entity( @PathVariable("entityConfig") String entityType,
	                             @PathVariable("entityId") long entityId,
	                             ModelMap model
	) throws Exception {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( entityType );
		Object original = entityConfiguration.getEntityModel().findOne( entityId );

		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "properties", loadProperties( entityConfiguration.getEntityType(), entityId ) );

		return null;//entityConfiguration.wrap( original );
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String viewProperties(
			@ModelAttribute("entityConfig") EntityConfiguration entityConfiguration,
			@ModelAttribute("entity") EntityWrapper entity,
			@ModelAttribute("properties") EntityProperties properties,
			ModelMap model,
			AdminMenu adminMenu ) {
		model.addAttribute( "tab", "properties" );

		adminMenu.getLowestSelectedItem()
		         .addItem( "/selectedEntity", entity.getEntityLabel() )
		         .setSelected( true );

		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu(
				                    new EntityAdminMenu(
						                    entityConfiguration.getEntityType(),
						                    entity.getEntity()
				                    )
		                    )
		);

		Collection<FormPropertyDescriptor> descriptors = new ArrayList<>();

		EntityPropertiesDescriptor descriptor = getDescriptor( entityConfiguration.getEntityType() );

		for ( String propertyName : descriptor.registry().getRegisteredProperties() ) {
			FormPropertyDescriptor d = new FormPropertyDescriptor();
			d.setName( propertyName );
			d.setDisplayName( propertyName );
			d.setPropertyType( descriptor.registry().getTypeForProperty( propertyName ).getType() );
			d.setPropertyResolvableType( descriptor.registry().getTypeForProperty( propertyName ).getResolvableType() );

			descriptors.add( d );
		}

		EntityForm entityForm = formFactory.create( descriptors );

//		for ( FormElement element : entityForm.getElements() ) {
//			//element.setValue( properties.getValue( element.getName() ) );
//		}

		model.addAttribute( "entityForm", entityForm );

		return "th/entity/form";
	}
}
