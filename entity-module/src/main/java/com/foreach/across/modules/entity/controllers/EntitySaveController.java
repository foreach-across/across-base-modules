package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.business.EntityWrapper;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.Serializable;

@AdminWebController
@RequestMapping(EntityController.PATH + "/{entityConfig}")
public class EntitySaveController
{
	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private MenuFactory menuFactory;

	@Autowired
	private Validator entityValidatorFactory;

	@Autowired
	private ConversionService conversionService;

	@InitBinder
	protected void initBinder( WebDataBinder binder ) {
		binder.setValidator( entityValidatorFactory );
	}

	@SuppressWarnings("unchecked")
	@ModelAttribute(EntityView.ATTRIBUTE_ENTITY)
	@RequestMapping(value = "/{entityId}/update", method = RequestMethod.POST)
	public Object entity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                      @PathVariable("entityId") Serializable entityId,
	                      ModelMap model
	) throws Exception {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		Object entity = entityModel.createNew();
		Object original = conversionService.convert( entityId, entityConfiguration.getEntityType() );
		model.addAttribute( "original", original );

		if ( original != null ) {
			//model.addAttribute( "original", entityConfiguration.wrap( original ) );
			entity = entityModel.createDto( original );
		}

		return entity;
	}

	@ModelAttribute(EntityView.ATTRIBUTE_ENTITY)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Object entity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration ) throws Exception {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		return entityModel.createNew();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/create", "/{entityId}/update" }, method = RequestMethod.POST)
	public ModelAndView saveEntity( @PathVariable("entityConfig") EntityConfiguration entityConfiguration,
	                                @ModelAttribute(EntityView.ATTRIBUTE_ENTITY) @Valid Object entity,
	                                BindingResult bindingResult,
	                                Model model,
	                                AdminMenu adminMenu ) {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		if ( !bindingResult.hasErrors() ) {
			entityModel.save( entity );

			ModelAndView mav = new ModelAndView();
			mav.setViewName(
					"redirect:" + entityConfiguration.getAttribute( EntityLinkBuilder.class ).update( entity )
			);

			return mav;
		}
		else {
			EntityWrapper originalEntity = (EntityWrapper) model.asMap().get( "original" );

			if ( originalEntity != null ) {
				adminMenu.getLowestSelectedItem()
				         .addItem( "/selectedEntity", originalEntity.getEntityLabel() )
				         .setSelected( true );
				model.addAttribute( "entityMenu",
				                    menuFactory.buildMenu(
						                    new EntityAdminMenu(
								                    entityConfiguration.getEntityType(),
								                    originalEntity.getEntity()
						                    )
				                    )
				);
			}
			else {
				model.addAttribute( "entityMenu",
				                    menuFactory.buildMenu(
						                    new EntityAdminMenu( entityConfiguration.getEntityType() )
				                    )
				);
			}

			EntityViewFactory viewFactory = entityConfiguration.getViewFactory(
					entityModel.isNew( entity ) ? EntityFormView.CREATE_VIEW_NAME : EntityFormView.UPDATE_VIEW_NAME
			);

			return viewFactory.create( entityConfiguration, model );
		}
	}
}
