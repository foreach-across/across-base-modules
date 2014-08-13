package com.foreach.across.modules.spring.security.config;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurer;
import com.foreach.across.modules.spring.security.configuration.WebSecurityConfigurerWrapper;
import com.foreach.across.modules.spring.security.configuration.WebSecurityConfigurerWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.util.ClassUtils;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Configures Spring security support in an AcrossWeb enabled context.
 */
@Configuration
@EnableWebMvcSecurity
@AcrossDepends(required = "AcrossWebModule")
public class AcrossWebSecurityConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( AcrossWebSecurityConfiguration.class );

	private static final String CLASS_THYMELEAF_TEMPLATE_ENGINE = "org.thymeleaf.spring4.SpringTemplateEngine";
	private static final String CLASS_SPRING_SECURITY_DIALECT =
			"org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@PostConstruct
	public void registerThymeleafDialect() {
		if ( shouldRegisterThymeleafDialect() ) {
			LOG.debug( "Registering Thymeleaf Spring security dialect" );

			Object springTemplateEngine = applicationContext.getBean( "springTemplateEngine" );

			if ( springTemplateEngine instanceof SpringTemplateEngine ) {
				( (SpringTemplateEngine) springTemplateEngine ).addDialect( new SpringSecurityDialect() );
				LOG.debug( "Thymeleaf Spring security dialect registered successfully." );
			}
			else {
				LOG.warn(
						"Unable to register Thymeleaf Spring security dialect as bean springTemplateEngine is not of the right type." );
			}
		}
	}

	private boolean shouldRegisterThymeleafDialect() {
		if ( applicationContext.containsBean( "springTemplateEngine" ) ) {
			ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

			if ( ClassUtils.isPresent( CLASS_THYMELEAF_TEMPLATE_ENGINE, threadClassLoader ) && ClassUtils.isPresent(
					CLASS_SPRING_SECURITY_DIALECT, threadClassLoader ) ) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Support using SpringSecurityConfigurer instances from other modules.
	 * This overrides the bean definition in {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration}
	 * and assembles a collection of WebSecurityConfigurers.  WebSecurityConfigurer instances present in this
	 * context are added, along with SpringSecurityWebConfigurer instances that are wrapped as WebSecurityConfigurer.
	 */
	@Bean(name = "autowiredWebSecurityConfigurersIgnoreParents")
	public Set<WebSecurityConfigurer> autowiredWebSecurityConfigurersIgnoreParents() {
		Collection<SpringSecurityWebConfigurer> configurers =
				contextBeanRegistry.getBeansOfType( SpringSecurityWebConfigurer.class, true );

		WebSecurityConfigurerSet webSecurityConfigurers = new WebSecurityConfigurerSet();
		webSecurityConfigurers.addAll( applicationContext.getBeansOfType( WebSecurityConfigurer.class ).values() );

		int index = 1;
		for ( SpringSecurityWebConfigurer configurer : configurers ) {
			WebSecurityConfigurerWrapper wrapper = webSecurityConfigurerWrapperFactory().createWrapper( configurer,
			                                                                                            index++ );

			webSecurityConfigurers.add( wrapper );
		}

		if ( webSecurityConfigurers.isEmpty() ) {
			throw new IllegalStateException(
					"At least one non-null instance of SpringSecurityWebConfigurer should be present in the Across context." );
		}

		return webSecurityConfigurers;
	}

	@Bean
	WebSecurityConfigurerWrapperFactory webSecurityConfigurerWrapperFactory() {
		return new WebSecurityConfigurerWrapperFactory();
	}

	/**
	 * Wrapping class that exposes the property used in
	 * {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration}.
	 */
	class WebSecurityConfigurerSet extends HashSet<WebSecurityConfigurer>
	{
		public List<WebSecurityConfigurer> getWebSecurityConfigurers() {
			return new ArrayList<>( this );
		}
	}
}

