package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.Serializable;

@AdminWebController
@RequestMapping(EntityController.PATH + "/{entityConfig}/{entityId}/update")
public class EntityUpdateController extends EntityControllerSupport
{
	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private MenuFactory menuFactory;

	@Autowired
	private ConversionService conversionService;

	@SuppressWarnings("unchecked")
	@ModelAttribute(EntityView.ATTRIBUTE_ENTITY)
	public Object entity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                      @PathVariable("entityId") Serializable entityId,
	                      Model model
	) {
		EntityModel entityModel = entityConfiguration.getEntityModel();
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );

		model.addAttribute( EntityFormView.ATTRIBUTE_ORIGINAL_ENTITY, entity );

		return entityModel.createDto( entity );
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showUpdateEntityForm( @PathVariable("entityConfig") EntityConfiguration entityConfiguration,
	                                          @ModelAttribute(EntityView.ATTRIBUTE_ENTITY) Object entity,
	                                          AdminMenu adminMenu,
	                                          Model model ) {
		Object original = model.asMap().get( EntityFormView.ATTRIBUTE_ORIGINAL_ENTITY );

		adminMenu.breadcrumbLeaf( entityConfiguration.getLabel( original ) );

		EntityViewFactory viewFactory = entityConfiguration.getViewFactory( EntityFormView.UPDATE_VIEW_NAME );
		EntityView view = viewFactory.create( entityConfiguration, model );
		view.setEntityMenu(
				menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(),
				                                            original ) )
		);

		if ( view.getPageTitle() == null ) {
			view.setPageTitle(
					view.getEntityMessages().updatePageTitle( entityConfiguration.getLabel( original ) )
			);
		}

		return view;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView saveEntity( @PathVariable("entityConfig") EntityConfiguration entityConfiguration,
	                                @ModelAttribute(EntityView.ATTRIBUTE_ENTITY) @Valid Object entity,
	                                BindingResult bindingResult,
	                                Model model,
	                                AdminMenu adminMenu,
	                                RedirectAttributes redirectAttributes ) {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		if ( !bindingResult.hasErrors() ) {
			entityModel.save( entity );

			ModelAndView mav = new ModelAndView();
			mav.setViewName(
					adminWeb.redirect( entityConfiguration.getAttribute( EntityLinkBuilder.class ).update( entity ) )
			);

			redirectAttributes.addFlashAttribute( "successMessage", "feedback.entityUpdated" );

			return mav;
		}
		else {
			return showUpdateEntityForm( entityConfiguration, entity, adminMenu, model );
		}
	}
}
