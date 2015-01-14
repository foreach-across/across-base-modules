package com.foreach.across.modules.entity.services;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.entity.business.EntityWrapper;
import com.foreach.across.modules.entity.business.MutableEntityRegistry;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.generators.EntityLabelGenerator;
import com.foreach.across.modules.entity.generators.id.DefaultIdGenerator;
import com.foreach.across.modules.entity.generators.label.PropertyLabelGenerator;
import com.foreach.across.modules.entity.generators.label.ToStringLabelGenerator;
import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains the registered entity definitions that are manageable.
 */
@Service
public class EntityRegistryImpl implements MutableEntityRegistry
{
	private static final Logger LOG = LoggerFactory.getLogger( EntityRegistryImpl.class );

	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	@Autowired(required = false)
	private ConfigurableConversionService conversionService;

	private Collection<EntityConfiguration> entityConfigurations = new ArrayList<>();

	@Override
	public Collection<EntityConfiguration> getEntities() {
		return entityConfigurations;
	}

	@Override
	public void register( EntityConfiguration entityConfiguration ) {
		entityConfigurations.add( entityConfiguration );
	}

	@Override
	public boolean contains( Class entityType ) {
		return getEntityConfiguration( entityType ) != null;
	}

	@Override
	public EntityConfiguration getEntityConfiguration( Class entityType ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( configuration.getEntityType().equals( entityType ) ) {
				return configuration;
			}
		}

		return null;
	}

	//	@PostRefresh
	protected void buildRegistry() {
		Collection<EntityConfigurer> configurers = beanRegistry.getBeansOfType( EntityConfigurer.class, true );

		// Scan all repositories
		Collection<BasicRepository> repositories = beanRegistry.getBeansOfType( BasicRepository.class, true );

		// Register the entity classes
		List<EntityConfiguration> entities = new ArrayList<>( repositories.size() );

		for ( final BasicRepository repository : repositories ) {
			if ( isAllowedEntity( repository.getEntityClass() ) ) {
				EntityConfiguration entityConfiguration = new EntityConfiguration( repository );

				if ( entityConfiguration.getLabelGenerator() == null ) {
					EntityLabelGenerator generator = PropertyLabelGenerator.forProperty( repository.getEntityClass(),
					                                                                     "name" );
					if ( generator == null ) {
						generator = PropertyLabelGenerator.forProperty( repository.getEntityClass(), "title" );
					}

					if ( generator == null ) {
						generator = new ToStringLabelGenerator();
					}

					if ( conversionService != null && !conversionService.canConvert( String.class, repository.getEntityClass() ) ) {
						conversionService.addConverter( String.class, repository.getEntityClass(),
						                                new Converter<Object, Object>()
						                                {
							                                @Override
							                                public Object convert( Object source ) {
								                                if ( source == null || StringUtils.isBlank(
										                                source.toString() ) ) {
									                                return null;
								                                }
								                                return repository.getById( Long.parseLong(
										                                source.toString() ) );
							                                }
						                                } );
					}

					entityConfiguration.setLabelGenerator( generator );
				}

				entityConfiguration.setIdGenerator( new DefaultIdGenerator() );

				/*
				for( EntityConfigurer configurer : configurers ) {
					if ( configurer.accepts( repository.getEntityClass() ) ) {
						configurer.configure( entityConfiguration );
					}
				}
				*/

				entities.add( entityConfiguration );
			}
		}

		Collections.sort( entities );

		this.entityConfigurations = entities;
	}

	private boolean isAllowedEntity( Class<?> clazz ) {
		return !Modifier.isInterface( clazz.getModifiers() ) && !Modifier.isAbstract( clazz.getModifiers() );
	}

	public EntityConfiguration getEntityByPath( String entityType ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( StringUtils.equals( entityType, configuration.getPath() ) ) {
				return configuration;
			}
		}

		return null;
	}

	public EntityConfiguration getEntityByClass( Class entityType ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( configuration.getEntityType().equals( entityType ) ) {
				return configuration;
			}
		}

		return null;
	}

	public EntityWrapper wrap( Object entity ) {
		EntityConfiguration config = getEntityByClass( ClassUtils.getUserClass( entity ) );

		if ( config == null ) {
			config = new EntityConfiguration( ClassUtils.getUserClass( entity ) );
		}

		return config.wrap( entity );
	}
}
