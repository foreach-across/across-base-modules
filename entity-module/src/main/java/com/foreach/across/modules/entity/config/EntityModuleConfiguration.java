package com.foreach.across.modules.entity.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.converters.EntityConverter;
import com.foreach.across.modules.entity.converters.StringToEntityConfigurationConverter;
import com.foreach.across.modules.entity.registrars.ModuleEntityRegistration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistries;
import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.entity.services.EntityFormRenderer;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.support.ConfigurableConversionService;

import javax.annotation.PostConstruct;

@Configuration
public class EntityModuleConfiguration
{
	@Autowired(required = false)
	private ConfigurableConversionService conversionService;

	@PostConstruct
	public void registerConverters() {
		if ( conversionService != null ) {
			EntityRegistry entityRegistry = entityRegistry();

			conversionService.addConverter( new StringToEntityConfigurationConverter( entityRegistry ) );
			conversionService.addConverter( new EntityConverter<>( conversionService, entityRegistry ) );
		}
	}

	@Bean
	public EntityRegistryImpl entityRegistry() {
		return new EntityRegistryImpl();
	}

	/**
	 * Ensures modules can configure entities through either EntityRegistrar or EntityConfigurer beans.
	 */
	@Bean
	public ModuleEntityRegistration moduleEntityRegistration() {
		return new ModuleEntityRegistration();
	}

	@Bean
	public EntityPropertyRegistries entityPropertyRegistries() {
		return new EntityPropertyRegistries();
	}

	@Bean
	public EntityFormFactory entityFormFactory() {
		return new EntityFormFactory();
	}

	@Bean
	public EntityFormRenderer entityFormRenderer() {
		return new EntityFormRenderer();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename( EntityModule.class.getPackage().getName() + ".messages.EntityModule" );

		return messageSource;
	}

	@Bean
	@Exposed
	@Scope("prototype")
	public EntityListViewFactory entityListViewFactory() {
		return new EntityListViewFactory();
	}

	@Bean
	@Exposed
	@Scope("prototype")
	public EntityFormViewFactory entityCreateViewFactory() {
		return new EntityFormViewFactory();
	}

/*
	@Bean
	public LocalValidatorFactoryBean entityValidatorFactory() {
		return new LocalValidatorFactoryBean();
	}
	*/
}
