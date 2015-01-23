package com.foreach.across.modules.entity.registry;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Contains the registered entity definitions that are manageable.
 * Every registered {@link com.foreach.across.modules.entity.registry.MutableEntityConfiguration} must have
 * a unique name ({@link EntityConfiguration#getName()}) and entity type ({@link EntityConfiguration#getEntityType()}).
 */
@Service
public class EntityRegistryImpl implements MutableEntityRegistry
{
	private static final Comparator<EntityConfiguration> DISPLAYNAME_COMPARATOR = new Comparator<EntityConfiguration>()
	{
		@Override
		public int compare( EntityConfiguration left, EntityConfiguration right ) {
			return ObjectUtils.compare( left.getDisplayName(), right.getDisplayName() );
		}
	};

	private static final Logger LOG = LoggerFactory.getLogger( EntityRegistryImpl.class );

	private final List<EntityConfiguration> entityConfigurations = new ArrayList<>();

	@Override
	public Collection<EntityConfiguration> getEntities() {
		return Collections.unmodifiableList( entityConfigurations );
	}

	@Override
	public void register( MutableEntityConfiguration<?> entityConfiguration ) {
		Assert.notNull( entityConfiguration );
		Assert.notNull( entityConfiguration.getEntityType() );
		Assert.notNull( entityConfiguration.getName() );

		EntityConfiguration existingByName = getEntityConfiguration( entityConfiguration.getName() );

		if ( existingByName != null && existingByName != entityConfiguration ) {
			throw new IllegalArgumentException( "There is another EntityConfiguration for " + existingByName
					.getEntityType() + " with name " + existingByName.getName() );
		}

		EntityConfiguration existingByType = getEntityConfiguration( entityConfiguration.getEntityType() );

		if ( existingByType != null && existingByType != entityConfiguration ) {
			throw new IllegalArgumentException( "There is another EntityConfiguration for " + existingByType
					.getEntityType() + " with name " + existingByType.getName() );
		}

		if ( existingByName == entityConfiguration || existingByType == entityConfiguration ) {
			LOG.trace( "Attempt to re-register EntityConfiguration for " + entityConfiguration
					.getEntityType() + " with name " + entityConfiguration.getName() );
			return;
		}

		if ( entityConfiguration.getEntityModel() == null ) {
			LOG.warn( "Registering entity type {} without entity model - functionality will be limited",
			          entityConfiguration.getEntityType() );
		}

		entityConfigurations.add( entityConfiguration );

		Collections.sort( entityConfigurations, DISPLAYNAME_COMPARATOR );
	}

	@Override
	public boolean contains( Class<?> entityType ) {
		return getEntityConfiguration( entityType ) != null;
	}

	@Override
	public boolean contains( String entityName ) {
		return getEntityConfiguration( entityName ) != null;
	}

	@Override
	public <T> MutableEntityConfiguration<T> remove( String entityName ) {
		MutableEntityConfiguration<T> registered = (MutableEntityConfiguration<T>) getEntityConfiguration( entityName );

		if ( registered != null ) {
			entityConfigurations.remove( registered );
		}

		return registered;
	}

	@Override
	public <T> MutableEntityConfiguration<T> remove( Class<T> entityType ) {
		MutableEntityConfiguration<T> registered = getMutableEntityConfiguration( entityType );

		if ( registered != null ) {
			entityConfigurations.remove( registered );
		}

		return registered;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EntityConfiguration<T> getEntityConfiguration( Class<T> entityType ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( configuration.getEntityType().equals( entityType ) ) {
				return (EntityConfiguration<T>) configuration;
			}
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EntityConfiguration<T> getEntityConfiguration( String entityName ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( StringUtils.equals( configuration.getName(), entityName ) ) {
				return (EntityConfiguration<T>) configuration;
			}
		}

		return null;
	}

	@Override
	public <T> MutableEntityConfiguration<T> getMutableEntityConfiguration( Class<T> entityType ) {
		return (MutableEntityConfiguration<T>) getEntityConfiguration( entityType );
	}
}
