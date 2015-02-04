package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;

@AdminWebController
@RequestMapping(EntityController.PATH)
public class EntityController extends EntityControllerSupport
{
	public static final String PATH = "/entities";

	@Autowired
	private EntityRegistryImpl entityRegistry;

	@Autowired
	private ConversionService conversionService;

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

	@RequestMapping("/{entityConfig}/{entityId}")
	public ModelAndView renderEntityView( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                                      @PathVariable("entityId") Serializable entityId,
	                                      @RequestParam(value = "view") String viewName,
	                                      Model model
	) {
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		EntityViewFactory view = entityConfiguration.getViewFactory( viewName );

		return view.create( entityConfiguration, model );
	}
}
