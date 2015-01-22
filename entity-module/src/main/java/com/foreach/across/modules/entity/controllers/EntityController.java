package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityView;
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
@RequestMapping(EntityController.PATH)
public class EntityController
{
	public static final String PATH = "/entities";

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
		model.addAttribute( EntityListView.ATTRIBUTE_PAGEABLE, pageable );

		EntityViewFactory view = entityConfiguration.getViewFactory( EntityListView.VIEW_NAME );

		return view.create( entityConfiguration, model );
	}

	@RequestMapping(value = "/{entityConfig}/create", method = RequestMethod.GET)
	public ModelAndView createEntity(
			@PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
			Model model ) throws Exception {
		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu<>( entityConfiguration.getEntityType() ) ) );

		EntityViewFactory view = entityConfiguration.getViewFactory( EntityFormView.CREATE_VIEW_NAME );
		return view.create( entityConfiguration, model );
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{entityConfig}/{entityId}/update", method = RequestMethod.GET)
	public ModelAndView modifyEntity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                                  @PathVariable("entityId") Serializable entityId,
	                                  AdminMenu adminMenu,
	                                  Model model ) throws Exception {
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		//EntityWrapper entity = entityConfiguration.wrap( entityConfiguration.getRepository().getById( entityId ) );
		//adminMenu.getLowestSelectedItem().addItem( "/selectedEntity", entity.getEntityLabel() ).setSelected( true );

		model.addAttribute( "entityMenu",
		                    menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(),
		                                                                entity ) ) );

		EntityViewFactory view = entityConfiguration.getViewFactory( EntityFormView.UPDATE_VIEW_NAME );
		return view.create( entityConfiguration, model );
	}
}
