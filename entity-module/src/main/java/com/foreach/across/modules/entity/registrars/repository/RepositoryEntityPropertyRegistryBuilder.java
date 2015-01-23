package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} for a
 * {@link org.springframework.data.repository.core.support.RepositoryFactoryInformation} bean.
 * <p/>
 * Puts every EntityPropertyRegistry in the central registry so properties of associated entities
 * can be determined as well.
 */
public class RepositoryEntityPropertyRegistryBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( RepositoryEntityPropertyRegistryBuilder.class );

	@Autowired
	private EntityPropertyRegistries entityPropertyRegistries;

	public <T> void buildEntityPropertyRegistry( MutableEntityConfiguration<T> entityConfiguration ) {
		Class<T> entityType = entityConfiguration.getEntityType();
		RepositoryFactoryInformation<T, ?> repositoryFactoryInformation
				= entityConfiguration.getAttribute( RepositoryFactoryInformation.class );

		MutableEntityPropertyRegistry registry =
				(MutableEntityPropertyRegistry) entityPropertyRegistries.getRegistry( entityType );

		configureSortableProperties( registry, repositoryFactoryInformation.getPersistentEntity() );
		configureDefaultFilter( entityType, registry );
		configureDefaultOrder( entityType, registry );
		configureKnownDescriptors( entityType, registry );

		entityConfiguration.setPropertyRegistry( registry );
	}

	private void configureDefaultFilter( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		if ( registry.getDefaultFilter() == null ) {
			List<String> excludedProps = new LinkedList<>();
			excludedProps.add( "class" );

			if ( Persistable.class.isAssignableFrom( entityType ) ) {
				excludedProps.add( "new" );
			}

			if ( SettableIdBasedEntity.class.isAssignableFrom( entityType ) ) {
				excludedProps.add( "newEntityId" );
			}

			registry.setDefaultFilter( EntityPropertyFilters.exclude( excludedProps ) );
		}
	}

	private void configureDefaultOrder( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		Map<String, Integer> propertiesOrder = new HashMap<>();

		if ( Auditable.class.isAssignableFrom( entityType ) ) {
			propertiesOrder.put( "createdDate", 1 );
			propertiesOrder.put( "createdBy", 2 );
			propertiesOrder.put( "lastModifiedDate", 3 );
			propertiesOrder.put( "lastModifiedBy", 4 );
		}

		registry.setDefaultOrder( new EntityPropertyOrder( propertiesOrder ) );
	}

	private void configureKnownDescriptors( Class<?> entityType, MutableEntityPropertyRegistry registry ) {

		if ( Persistable.class.isAssignableFrom( entityType ) ) {
			registry.getMutableProperty( "id" ).setHidden( true );
		}

		if ( SettableIdBasedEntity.class.isAssignableFrom( entityType ) ) {
			MutableEntityPropertyDescriptor mutable = registry.getMutableProperty( "newEntityId" );
			mutable.setReadable( false );
			mutable.setHidden( true );
		}

		if ( Auditable.class.isAssignableFrom( entityType ) ) {
			registry.getMutableProperty( "createdDate" ).setDisplayName( "Created" );
			registry.getMutableProperty( "lastModifiedDate" ).setDisplayName( "Last modified" );

			// Auditable properties are set automatically, should not be set through entity
			registry.getMutableProperty( "createdBy" ).setWritable( false );
			registry.getMutableProperty( "createdDate" ).setWritable( false );
			registry.getMutableProperty( "lastModifiedBy" ).setWritable( false );
			registry.getMutableProperty( "lastModifiedDate" ).setWritable( false );
		}
	}

	private void configureSortableProperties( MutableEntityPropertyRegistry registry,
	                                          PersistentEntity<?, ?> persistentEntity ) {
		LOG.trace( "Finding sortable properties for entity {}", persistentEntity.getType() );

		// A property is sortable by default if it is persisted
		for ( EntityPropertyDescriptor descriptor : registry.getRegisteredDescriptors() ) {
			PersistentProperty persistentProperty = persistentEntity.getPersistentProperty( descriptor.getName() );

			if ( persistentProperty != null && !persistentProperty.isTransient() ) {
				LOG.trace( "Setting persisted property {} as sortable", persistentProperty.getName() );

				MutableEntityPropertyDescriptor mutable = registry.getMutableProperty( descriptor.getName() );

				if ( mutable != null ) {
					mutable.addAttribute( EntityAttributes.SORTABLE_PROPERTY, persistentProperty.getName() );
				}
			}
		}
	}
}
