package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.business.EntityForm;
import com.foreach.across.modules.entity.business.EntityWrapper;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.Serializable;

@AdminWebController
@RequestMapping(EntityController.PATH + "/{entityConfig}")
public class EntitySaveController
{
	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private EntityFormFactory formFactory;

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
	@ModelAttribute("entity")
	public Object entity( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                      @RequestParam(value = "id", required = false) String entityId,
	                      ModelMap model
	) throws Exception {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		Object entity = entityModel.createNew();

		if ( !StringUtils.isBlank( entityId ) ) {
			Serializable coercedEntityId = (Serializable) conversionService.convert( entityId,
			                                                                         entityModel.getIdType() );
			if ( coercedEntityId != null ) {
				Object original = entityModel.findOne( coercedEntityId );

				if ( original != null ) {
					//model.addAttribute( "original", entityConfiguration.wrap( original ) );
					entity = entityModel.createDto( original );
				}
			}
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/*", method = RequestMethod.POST)
	public String saveEntity( @PathVariable("entityConfig") EntityConfiguration entityConfiguration,
	                          @ModelAttribute("entity") @Valid Object entity,
	                          BindingResult bindingResult,
	                          ModelMap model,
	                          AdminMenu adminMenu,
	                          RedirectAttributes re ) {
		if ( !bindingResult.hasErrors() ) {
			entityConfiguration.getEntityModel().save( entity );

			re.addAttribute( "entityId", ( (IdBasedEntity) entity ).getId() );

			return adminWeb.redirect( "/entities/" + entityConfiguration.getName() + "/{entityId}" );
		}
		else {
			EntityWrapper originalEntity = (EntityWrapper) model.get( "original" );

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

			EntityForm entityForm = formFactory.create( entityConfiguration );
			entityForm.setEntity( entity );

			model.addAttribute( "entityForm", entityForm );
			model.addAttribute( "existing", originalEntity != null );
			model.addAttribute( "entityConfig", entityConfiguration );
			//model.addAttribute( "entity", entityConfiguration.wrap( entity ) );
		}

		return "th/entity/edit";
	}

	private boolean isNewEntity( Object entity ) {
		return ( (IdBasedEntity) entity ).getId() == 0;
	}
}
