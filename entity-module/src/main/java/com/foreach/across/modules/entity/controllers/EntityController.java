package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.business.EntityWrapper;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.entity.services.EntityRegistry;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AdminWebController
@RequestMapping("/entities")
public class EntityController
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private EntityFormFactory formFactory;

	@Autowired
	private MenuFactory menuFactory;

	@RequestMapping
	public String listAllEntityTypes( Model model ) {
		model.addAttribute( "entities", entityRegistry.getEntities() );

		return "th/entity/overview";
	}

	@RequestMapping(value = "/{entityConfig}", method = RequestMethod.GET)
	public String listAllEntities( @PathVariable("entityConfig") String entityType,
	                               Model model ) {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityByPath( entityType );

		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "entities", entityConfiguration.getRepository().findAll() );

		return "th/entity/list";
	}

	@RequestMapping(value = "/{entityConfig}/create", method = RequestMethod.GET)
	public String createEntity( @PathVariable("entityConfig") String entityType,
	                            Model model ) throws Exception {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityByPath( entityType );

		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu<>( entityConfiguration.getEntityClass() ) ) );

		EntityForm entityForm = formFactory.create( entityConfiguration );
		entityForm.setEntity( entityConfiguration.getEntityClass().newInstance() );

		model.addAttribute( "entityForm", entityForm );
		model.addAttribute( "existing", false );
		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "entity", entityConfiguration.getEntityClass().newInstance() );

		return "th/entity/edit";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{entityConfig}/{entityId}", method = RequestMethod.GET)
	public String modifyEntity( @PathVariable("entityConfig") String entityType,
	                            @PathVariable("entityId") long entityId,
	                            AdminMenu adminMenu,
	                            Model model ) throws Exception {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityByPath( entityType );
		EntityWrapper entity = entityConfiguration.wrap( entityConfiguration.getRepository().getById( entityId ) );

		adminMenu.getLowestSelectedItem().addItem( "/selectedEntity", entity.getEntityLabel() ).setSelected( true );

		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityClass(),
		                                                                entity.getEntity() ) ) );

		EntityForm entityForm = formFactory.create( entityConfiguration );
		entityForm.setEntity( entity.getEntity() );

		model.addAttribute( "entityForm", entityForm );
		model.addAttribute( "existing", true );
		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "original", entity );
		model.addAttribute( "entity", entity.getEntity() );

		return "th/entity/edit";
	}
}
