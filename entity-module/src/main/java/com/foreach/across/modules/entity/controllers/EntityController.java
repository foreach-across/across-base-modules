package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;

@AdminWebController
@RequestMapping("/entities")
public class EntityController
{
	@Autowired
	private EntityRegistryImpl entityRegistry;

	@Autowired
	private EntityFormFactory formFactory;

	@Autowired
	private MenuFactory menuFactory;

	@Autowired
	private ConversionService conversionService;

	@ModelAttribute
	public void init( WebResourceRegistry registry ) {
		registry.addWithKey( WebResource.CSS, EntityModule.NAME, "/css/entity/entity-module.css", WebResource.VIEWS );
		registry.addWithKey( WebResource.JAVASCRIPT_PAGE_END, EntityModule.NAME,
		                     "/js/entity/entity-module.js", WebResource.VIEWS );
	}

	@RequestMapping
	public String listAllEntityTypes( Model model ) {
		model.addAttribute( "entities", entityRegistry.getEntities() );

		return "th/entity/overview";
	}

	@RequestMapping(value = "/{entityConfig}", method = RequestMethod.GET)
	public ModelAndView listAllEntities( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                                     Model model,
	                                     Pageable pageable
	) {
		EntityViewFactory view = entityConfiguration.getViewFactory( "crud-list" );

		model.addAttribute( "pageable", pageable );

		return view.create( entityConfiguration, model );

		/*
		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "entities", entityConfiguration.getRepository().findAll() );

		return "th/entity/list";
		*/
	}

	@RequestMapping(value = "/{entityConfig}/create", method = RequestMethod.GET)
	public String createEntity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                            Model model ) throws Exception {
		//EntityViewFactory view = entityConfiguration.getViewFactory( "crud-create" );

		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu<>( entityConfiguration.getEntityType() ) ) );

		EntityForm entityForm = formFactory.create( entityConfiguration );
		entityForm.setEntity( entityConfiguration.getEntityType().newInstance() );

		model.addAttribute( "entityForm", entityForm );
		model.addAttribute( "existing", false );
		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "entity", entityConfiguration.getEntityType().newInstance() );

		return "th/entity/edit";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{entityConfig}/{entityId}", method = RequestMethod.GET)
	public String modifyEntity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                            @PathVariable("entityId") Serializable entityId,
	                            AdminMenu adminMenu,
	                            Model model ) throws Exception {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		Serializable coercedEntityId = (Serializable) conversionService.convert( entityId, entityModel.getIdType() );
		Object entity = entityModel.findOne( coercedEntityId );

		//EntityWrapper entity = entityConfiguration.wrap( entityConfiguration.getRepository().getById( entityId ) );

		//adminMenu.getLowestSelectedItem().addItem( "/selectedEntity", entity.getEntityLabel() ).setSelected( true );

		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(),
		                                                                entity ) ) );

		EntityForm entityForm = formFactory.create( entityConfiguration );
		entityForm.setEntity( entity );

		model.addAttribute( "entityForm", entityForm );
		model.addAttribute( "existing", true );
		model.addAttribute( "entityConfig", entityConfiguration );
		model.addAttribute( "original", entity );
		model.addAttribute( "entity", entity );

		return "th/entity/edit";
	}
}
