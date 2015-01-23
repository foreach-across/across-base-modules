package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@AdminWebController
@RequestMapping(EntityController.PATH)
public class EntityController extends EntityControllerSupport
{
	public static final String PATH = "/entities";

	@Autowired
	private EntityRegistryImpl entityRegistry;

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
}
