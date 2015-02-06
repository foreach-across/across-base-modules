package com.foreach.across.modules.entity.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import org.apache.commons.lang3.StringUtils;
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
	                                     Pageable pageable,
	                                     WebViewCreationContext creationContext
	) {
		creationContext.setEntityConfiguration( entityConfiguration );

		model.addAttribute( EntityListView.ATTRIBUTE_PAGEABLE, pageable );

		String viewName = EntityListView.VIEW_NAME;
		EntityViewFactory view = entityConfiguration.getViewFactory( viewName );

		return view.create( viewName, creationContext, model );
	}

	@RequestMapping("/{entityConfig}/{entityId}")
	public ModelAndView renderEntityView( @PathVariable("entityConfig") EntityConfiguration<?> entityConfiguration,
	                                      @PathVariable("entityId") Serializable entityId,
	                                      @RequestParam(value = "view") String viewName,
	                                      @RequestParam(value = WebTemplateInterceptor.PARTIAL_PARAMETER,
	                                                    required = false) String partialFragment,
	                                      Model model,
	                                      WebViewCreationContext creationContext
	) {
		creationContext.setEntityConfiguration( entityConfiguration );

		Object entity = conversionService.convert( entityId, entityConfiguration.getEntityType() );
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		EntityViewFactory viewFactory = entityConfiguration.getViewFactory( viewName );
		EntityView view = viewFactory.create( viewName, creationContext, model );

		if ( StringUtils.isNotBlank( partialFragment ) ) {
			view.setViewName( StringUtils.join( new Object[] { view.getViewName(), partialFragment },
			                                          " :: " ) );
		}

		return view;
	}
}
