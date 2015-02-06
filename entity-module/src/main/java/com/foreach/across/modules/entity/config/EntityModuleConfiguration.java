package com.foreach.across.modules.entity.config;

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.context.support.AcrossModuleMessageSource;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.converters.EntityConverter;
import com.foreach.across.modules.entity.converters.StringToEntityConfigurationConverter;
import com.foreach.across.modules.entity.registrars.ModuleEntityRegistration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistries;
import com.foreach.across.modules.entity.services.EntityFormService;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.elements.CommonViewElementTypeLookupStrategy;
import com.foreach.across.modules.entity.views.elements.fieldset.FieldsetElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.elements.form.checkbox.CheckboxFormElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.elements.form.hidden.HiddenFormElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.elements.form.select.SelectFormElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.elements.form.textbox.TextboxFormElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.elements.readonly.ConversionServiceViewElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.thymeleaf.EntityModuleDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;

@Configuration
public class EntityModuleConfiguration {
    private static final String CLASS_THYMELEAF_TEMPLATE_ENGINE = "org.thymeleaf.spring4.SpringTemplateEngine";
    private static final String CLASS_SPRING_SECURITY_DIALECT =
            "org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect";

    private static final Logger LOG = LoggerFactory.getLogger(EntityModuleConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private ConfigurableConversionService conversionService;

    @PostConstruct
    public void init() {
        ConfigurableConversionService converterRegistry =
                conversionService != null ? conversionService : entityConversionService();

        EntityRegistry entityRegistry = entityRegistry();

        converterRegistry.addConverter(new StringToEntityConfigurationConverter(entityRegistry));
        converterRegistry.addConverter(new EntityConverter<>(converterRegistry, entityRegistry));

        if (shouldRegisterThymeleafDialect()) {
            LOG.debug("Registering Thymeleaf entity module dialect");

            Object springTemplateEngine = applicationContext.getBean("springTemplateEngine");

            if (springTemplateEngine instanceof SpringTemplateEngine) {
                ((SpringTemplateEngine) springTemplateEngine).addDialect(new EntityModuleDialect());
                LOG.debug("Thymeleaf entity module dialect registered successfully.");
            } else {
                LOG.warn(
                        "Unable to register Thymeleaf entity module dialect as bean springTemplateEngine is not of the right type.");
            }
        }
    }

    @Bean
    public EntityRegistryImpl entityRegistry() {
        return new EntityRegistryImpl();
    }

    @Bean
    @Exposed
    @AcrossCondition("not hasBeanOfType(T(org.springframework.core.convert.ConversionService))")
    public ConfigurableConversionService entityConversionService() {
        LOG.warn("No ConversionService found in Across context - creating and exposing a ConversionService bean");
        return new DefaultFormattingConversionService();
    }

    @Bean(name = EntityModule.VALIDATOR)
    @Exposed
    @AcrossCondition("not hasBean('" + EntityModule.VALIDATOR + "')")
    public Validator entityValidator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Ensures modules can configure entities through either EntityRegistrar or EntityConfigurer beans.
     */
    @Bean
    public ModuleEntityRegistration moduleEntityRegistration() {
        return new ModuleEntityRegistration();
    }

    @Bean
    @Exposed
    public EntityPropertyRegistries entityPropertyRegistries() {
        return new EntityPropertyRegistries();
    }

    @Bean
    public EntityFormService entityFormService() {
        return new EntityFormService();
    }

    @Bean
    public TextboxFormElementBuilderFactoryAssembler textboxFormElementBuilderFactoryAssembler() {
        return new TextboxFormElementBuilderFactoryAssembler();
    }

    @Bean
    public ConversionServiceViewElementBuilderFactoryAssembler conversionServiceViewElementBuilderFactoryAssembler() {
        return new ConversionServiceViewElementBuilderFactoryAssembler();
    }

    @Bean
    public HiddenFormElementBuilderFactoryAssembler hiddenFormElementBuilderFactoryAsssembler() {
        return new HiddenFormElementBuilderFactoryAssembler();
    }

    @Bean
    public SelectFormElementBuilderFactoryAssembler selectFormElementBuilderFactoryAssembler() {
        return new SelectFormElementBuilderFactoryAssembler();
    }

    @Bean
    public CheckboxFormElementBuilderFactoryAssembler checkboxFormElementBuilderFactoryAssembler() {
        return new CheckboxFormElementBuilderFactoryAssembler();
    }

    @Bean
    public FieldsetElementBuilderFactoryAssembler fieldsetElementBuilderFactoryAssembler() {
        return new FieldsetElementBuilderFactoryAssembler();
    }

    @Bean
    public CommonViewElementTypeLookupStrategy commonFormElementTypeLookupStrategy() {
        return new CommonViewElementTypeLookupStrategy();
    }

    @Bean
    public MessageSource messageSource() {
        return new AcrossModuleMessageSource();
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


    private boolean shouldRegisterThymeleafDialect() {
        if (applicationContext.containsBean("springTemplateEngine")) {
            ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

            if (ClassUtils.isPresent(CLASS_THYMELEAF_TEMPLATE_ENGINE, threadClassLoader)) {
                return true;
            }

        }

        return false;
    }
/*
    @Bean
	public LocalValidatorFactoryBean entityValidatorFactory() {
		return new LocalValidatorFactoryBean();
	}
	*/
}
