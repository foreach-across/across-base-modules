/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.properties.repositories;

import com.foreach.across.core.revision.Revision;
import com.foreach.across.core.revision.RevisionBasedEntity;
import com.foreach.across.core.revision.RevisionBasedEntityManager;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Entity properties that supports revisions. repository supporting revisions.
 *
 * @author Arne Vandamme
 */
public class RevisionBasedEntityPropertiesRepository<T, R extends Revision<T>>
		extends RevisionBasedEntityManager<RevisionBasedEntityPropertiesRepository.PropertyRevision<T>, T, R>
{
	private static final String FILTER_FOR_REVISION = "first_revision >= 0 and first_revision <= ? " +
			"and (removal_revision = 0 or removal_revision > ?)";
	private static final String FILTER_FOR_LATEST = "first_revision >= 0 and removal_revision = 0";
	private static final String FILTER_FOR_LATEST_AND_DRAFTS = "(first_revision = -1 or removal_revision = 0)";

	private final String SQL_INSERT_PROPERTY;
	private final String SQL_SELECT_PROPERTIES;
	private final String SQL_DROP_PROPERTIES;
	private final String SQL_UPDATE_PROPERTY;
	private final String SQL_DELETE_PROPERTY;

	private final JdbcTemplate jdbcTemplate;

	public RevisionBasedEntityPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		jdbcTemplate = new JdbcTemplate( configuration.dataSource() );

		String table = configuration.tableName();
		String keyColumn = configuration.keyColumnName();

		SQL_INSERT_PROPERTY = String.format(
				"INSERT INTO %s (%s,property_name,property_value,first_revision,removal_revision,delete_for_revision) " +
						"VALUES (?,?,?,?,?,?)", table, keyColumn );
		SQL_SELECT_PROPERTIES = String.format(
				"SELECT property_name, property_value,first_revision,removal_revision,delete_for_revision " +
						"FROM %s WHERE %s = ?", table, keyColumn ) + " AND %s";

		SQL_UPDATE_PROPERTY = String.format(
				"UPDATE %s SET property_value = ?, first_revision = ?, removal_revision = ?, delete_for_revision = ? " +
						"WHERE %s = ? AND property_name = ? AND first_revision = ? AND removal_revision = ?",
				table, keyColumn
		);

		SQL_DELETE_PROPERTY = String.format(
				"DELETE FROM %s WHERE %s = ? AND property_name = ? AND first_revision = ? AND removal_revision = ?",
				table, keyColumn
		);

		SQL_DROP_PROPERTIES = String.format( "DELETE FROM %s WHERE %s = ?", table, keyColumn );
	}

	@Override
	protected void insert( PropertyRevision<T> entity ) {
		jdbcTemplate.update(
				SQL_INSERT_PROPERTY,
				entity.getOwner(),
				entity.getName(),
				entity.getValue(),
				entity.getFirstRevision(),
				entity.getRemovalRevision(),
				entity.isDeleteForRevision()
		);
	}

	@Override
	protected void update( PropertyRevision<T> entity,
	                       int currentFirstRevision,
	                       int currentLastRevision ) {
		jdbcTemplate.update(
				SQL_UPDATE_PROPERTY,
				entity.getValue(),
				entity.getFirstRevision(),
				entity.getRemovalRevision(),
				entity.isDeleteForRevision(),
				entity.getOwner(),
				entity.getName(),
				currentFirstRevision,
				currentLastRevision
		);
	}

	@Override
	protected void delete( PropertyRevision<T> entity ) {
		jdbcTemplate.update(
				SQL_DELETE_PROPERTY,
				entity.getOwner(),
				entity.getName(),
				entity.getFirstRevision(),
				entity.getRemovalRevision()
		);
	}

	@Override
	protected void deleteAllForOwner( T owner ) {
		jdbcTemplate.update(
				SQL_DROP_PROPERTIES,
				owner
		);
	}

	@Override
	protected Collection<PropertyRevision<T>> getAllForLatestRevision( T owner ) {
		return buildProperties(
				owner,
				jdbcTemplate.queryForList(
						String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_LATEST ),
						owner
				)
		);
	}

	@Override
	protected Collection<PropertyRevision<T>> getAllForSpecificRevision( T owner,
	                                                                     int revisionNumber ) {
		return buildProperties(
				owner,
				jdbcTemplate.queryForList(
						String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_REVISION ),
						owner, revisionNumber, revisionNumber
				)
		);
	}

	@Override
	protected Collection<PropertyRevision<T>> getAllForDraftRevision( T owner ) {
		return buildProperties(
				owner,
				jdbcTemplate.queryForList(
						String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_LATEST_AND_DRAFTS ),
						owner
				)
		);
	}

	@Override
	protected PropertyRevision<T> createEntityFromExisting( PropertyRevision<T> existing ) {
		PropertyRevision<T> candidate = new PropertyRevision<>();
		candidate.setOwner( existing.getOwner() );
		candidate.setName( existing.getName() );
		candidate.setValue( existing.getValue() );
		candidate.setDeleteForRevision( existing.isDeleteForRevision() );

		return candidate;
	}

	@Override
	protected void copyEntityValuesFromExisting( PropertyRevision<T> entity,
	                                             PropertyRevision<T> existing ) {
		entity.setValue( existing.getValue() );
	}

	@Transactional(readOnly = true)
	public StringPropertiesSource loadProperties( T owner, int revisionNumber ) {
		Collection<PropertyRevision<T>> entities = getEntitiesForRevision( owner, revisionNumber );

		Map<String, String> sourceMap = new HashMap<>();
		for ( PropertyRevision<T> property : entities ) {
			sourceMap.put( property.getName(), property.getValue() );
		}

		return new StringPropertiesSource( sourceMap );
	}

	@Transactional
	public StringPropertiesSource checkoutProperties( T owner, int revisionNumber ) {
		Collection<PropertyRevision<T>> entities = checkout( owner, revisionNumber );

		Map<String, String> sourceMap = new HashMap<>();
		for ( PropertyRevision<T> property : entities ) {
			sourceMap.put( property.getName(), property.getValue() );
		}

		return new StringPropertiesSource( sourceMap );
	}

	@Transactional
	public void saveProperties( StringPropertiesSource properties, T owner, int revisionNumber ) {
		Collection<PropertyRevision<T>> entities = new LinkedList<>();

		for ( Map.Entry<String, ?> entry : properties.getProperties().entrySet() ) {
			PropertyRevision<T> candidate = new PropertyRevision<>();
			candidate.setOwner( owner );
			candidate.setFirstRevision( revisionNumber );
			candidate.setRemovalRevision( revisionNumber );
			candidate.setName( entry.getKey() );

			Object value = entry.getValue();
			candidate.setValue( value != null ? value.toString() : null );

			entities.add( candidate );
		}

		saveEntitiesForRevision( entities, owner, revisionNumber );
	}

	@Override
	protected Collection<PropertyRevision<T>> convertToNewDtos( Collection<PropertyRevision<T>> entitiesForRevision ) {
		return entitiesForRevision;
	}

	private Collection<PropertyRevision<T>> buildProperties( T owner, List<Map<String, Object>> properties ) {
		List<PropertyRevision<T>> props = new LinkedList<>();

		for ( Map<String, Object> entry : properties ) {
			PropertyRevision<T> propertyRevision = new PropertyRevision<>();
			propertyRevision.setOwner( owner );
			propertyRevision.setName( (String) entry.get( "property_name" ) );
			propertyRevision.setValue( (String) entry.get( "property_value" ) );
			propertyRevision.setFirstRevision( toInteger( entry.get( "first_revision" ) ) );
			propertyRevision.setRemovalRevision( toInteger( entry.get( "removal_revision" ) ) );
			propertyRevision.setDeleteForRevision( toBoolean( entry.get( "delete_for_revision" ) ) );

			props.add( propertyRevision );
		}

		return props;
	}

	private boolean toBoolean( Object instance ) {
		if ( instance instanceof Boolean ) {
			return (Boolean) instance;
		}

		if ( instance instanceof Number ) {
			return ( (Number) instance ).intValue() == 1;
		}

		return false;
	}

	private int toInteger( Object numericObject ) {
		if ( numericObject instanceof Number ) {
			return ( (Number) numericObject ).intValue();
		}

		return numericObject != null ? Integer.valueOf( numericObject.toString() ) : 0;
	}

	static class PropertyRevision<T> implements RevisionBasedEntity<PropertyRevision<T>>
	{
		private T owner;
		private String name, value;
		private int firstRevision, removalRevision;
		private boolean deleteForRevision;

		public T getOwner() {
			return owner;
		}

		public void setOwner( T owner ) {
			this.owner = owner;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public void setValue( String value ) {
			this.value = value;
		}

		public void setFirstRevision( int firstRevision ) {
			this.firstRevision = firstRevision;
		}

		public void setRemovalRevision( int removalRevision ) {
			this.removalRevision = removalRevision;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public int getFirstRevision() {
			return firstRevision;
		}

		public int getRemovalRevision() {
			return removalRevision;
		}

		public boolean isDeleteForRevision() {
			return deleteForRevision;
		}

		public void setDeleteForRevision( boolean deleteForRevision ) {
			this.deleteForRevision = deleteForRevision;
		}

		public boolean isDraft() {
			return firstRevision == Revision.DRAFT;
		}

		@Override
		public Object getEntityIdentifier() {
			return getName();
		}

		@Override
		public boolean isDifferentVersionOf( PropertyRevision other ) {
			return !StringUtils.equals( value, other.value );
		}
	}
}
