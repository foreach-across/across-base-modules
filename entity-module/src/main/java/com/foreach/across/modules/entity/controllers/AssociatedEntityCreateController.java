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
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.AssociatedEntityLinkBuilder;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentProperty;
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

/**
 * @author niels
 * @since 9/02/2015
 */
@AdminWebController
public class AssociatedEntityCreateController extends EntityControllerSupport
{

	private static final String PATH = AssociatedEntityController.PATH + "/create";

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private MenuFactory menuFactory;

	@Autowired
	private ConversionService conversionService;

	@SuppressWarnings("unchecked")
	@ModelAttribute(EntityView.ATTRIBUTE_ENTITY)
	public Object entity( @PathVariable(
			AssociatedEntityController.PATH_ASSOCIATED_ENTITY_CONFIG) EntityConfiguration<?> associatedEntityConfiguration,
	                      @PathVariable(
			                      AssociatedEntityController.PATH_ENTITY_CONFIG) EntityConfiguration<?> entityConfiguration,
	                      @PathVariable(AssociatedEntityController.PATH_ENTITY_ID) Serializable entityId,
	                      Model model
	) {
		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );

		model.addAttribute( EntityFormView.ATTRIBUTE_PARENT_ENTITY, entity );

		EntityModel associatedEntityModel = associatedEntityConfiguration.getEntityModel();
		BeanWrapper associatedBeanWrapper = new BeanWrapperImpl( associatedEntityModel.createNew() );
		associatedBeanWrapper.setPropertyValue(
				entityConfiguration.association( associatedEntityConfiguration.getEntityType() ).getAttribute(
						PersistentProperty.class ).getName(), entity );

		return associatedBeanWrapper.getWrappedInstance();
	}

	@RequestMapping(value = PATH, method = RequestMethod.GET)
	public ModelAndView showCreateAssociatedEntityForm( @PathVariable(
			AssociatedEntityController.PATH_ENTITY_CONFIG) EntityConfiguration entityConfiguration,
	                                                    @ModelAttribute(EntityView.ATTRIBUTE_ENTITY) Object entity,
	                                                    @PathVariable(
			                                                    AssociatedEntityController.PATH_ASSOCIATED_ENTITY_CONFIG) EntityConfiguration associatedConfig,
	                                                    @ModelAttribute(
			                                                    EntityFormView.ATTRIBUTE_PARENT_ENTITY) Object parentEntity,
	                                                    AdminMenu adminMenu,
	                                                    Model model,
	                                                    WebViewCreationContext creationContext ) {

		creationContext.setEntityConfiguration( associatedConfig );
		adminMenu.breadcrumbLeaf( entityConfiguration.getLabel( entity ) );
		EntityViewFactory viewFactory = entityConfiguration.association( associatedConfig.getEntityType() )
		                                                   .getViewFactory( EntityFormView.CREATE_VIEW_NAME );
		EntityView entityView = viewFactory.create( EntityFormView.CREATE_VIEW_NAME, creationContext, model );
		entityView.setPageTitle( new EntityMessages( entityConfiguration.getEntityMessageCodeResolver() )
				                         .updatePageTitle( entityConfiguration.getLabel( entity ) ) );
		entityView.setEntityMenu(
				menuFactory.buildMenu( new EntityAdminMenu( associatedConfig.getEntityType(), entity ) ) );

		return entityView;
	}

	@RequestMapping(value = PATH, method = RequestMethod.POST)
	public ModelAndView saveEntity( @PathVariable(
			AssociatedEntityController.PATH_ENTITY_CONFIG) EntityConfiguration entityConfiguration,
	                                @ModelAttribute(EntityView.ATTRIBUTE_ENTITY) @Valid Object entity,
	                                @ModelAttribute(EntityFormView.ATTRIBUTE_PARENT_ENTITY) Object parentEntity,
	                                @PathVariable(
			                                AssociatedEntityController.PATH_ASSOCIATED_ENTITY_CONFIG) EntityConfiguration associatedConfig,
	                                BindingResult bindingResult,
	                                AdminMenu adminMenu,
	                                Model model,
	                                RedirectAttributes redirectAttributes,
	                                WebViewCreationContext creationContext ) {
		EntityModel entityModel = associatedConfig.getEntityModel();
		if ( !bindingResult.hasErrors() ) {
			entityModel.save( entity );
			ModelAndView mav = new ModelAndView();
			mav.setViewName(
					adminWeb.redirect( entityConfiguration.getAttribute( AssociatedEntityLinkBuilder.class ).overview(
							entityConfiguration.getId( parentEntity ) ) ) );

			redirectAttributes.addFlashAttribute( "successMessage", "feedback.entityCreated" );
			return mav;
		}
		else {
			return showCreateAssociatedEntityForm( entityConfiguration, entity, associatedConfig, parentEntity,
			                                       adminMenu, model, creationContext );
		}
	}

}
