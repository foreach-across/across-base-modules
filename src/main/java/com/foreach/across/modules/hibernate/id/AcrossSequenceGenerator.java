package com.foreach.across.modules.hibernate.id;

import com.foreach.across.core.database.AcrossSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Custom TableGenerator strategy for generating long ids.
 * Has support for manually defining an id value and inserting an entity with that value instead.
 * Note that the repository code should support this behavior correctly, and special care should be
 * taken to avoid manual ids interfering with the sequence values.
 * <p/>
 * <p/>
 * <pre>
 *     &#064;GeneratedValue(generator = "seq_um_user_id")
 * 	   &#064;GenericGenerator(
 * 			name = "seq_um_user_id",
 * 			strategy = "com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator",
 * 			parameters = {
 * 					&#064;Parameter(name = "sequenceName", value = "seq_um_user_id"),
 * 					&#064;Parameter(name = "allocationSize", value = "10")
 *            }
 * 	    )
 * </pre>
 * The following parameters can be configured:
 * <ul>
 * <li>sequenceName: mandatory, name of the sequence in the sequences table</li>
 * <li>allowPredefinedIds: true if the generator should support manually inserting ids (default: true)</li>
 * <li>allocationSize: sequence allocation size (default: 50)</li>
 * <li>initialValue: initial sequence value (default: 1)</li>
 * </ul>
 *
 * @see com.foreach.across.core.installers.AcrossSequencesInstaller
 */
public class AcrossSequenceGenerator extends TableGenerator
{
	// Keep this as a constant string!
	public static final String STRATEGY = "com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator";

	private String entityName;

	private boolean supportPredefinedIds = true;

	@Override
	public void configure( Type type, Properties params, Dialect dialect ) throws MappingException {
		entityName = params.getProperty( ENTITY_NAME );
		if ( entityName == null ) {
			throw new MappingException( "no entity name" );
		}

		String pkColumnValue = params.getProperty( "sequenceName" );

		if ( StringUtils.isBlank( pkColumnValue ) ) {
			throw new MappingException( "A sequenceName is required for a Across sequence generator" );
		}

		Properties props = new Properties();
		props.putAll( params );
		props.put( SCHEMA, "" );
		props.put( CATALOG, "" );
		props.put( CONFIG_PREFER_SEGMENT_PER_ENTITY, "true" );
		props.put( TABLE_PARAM, AcrossSchemaConfiguration.TABLE_SEQUENCES );
		props.put( SEGMENT_COLUMN_PARAM, AcrossSchemaConfiguration.SEQUENCE_NAME );
		props.put( SEGMENT_VALUE_PARAM, pkColumnValue );
		props.put( VALUE_COLUMN_PARAM, AcrossSchemaConfiguration.SEQUENCE_VALUE );
		props.put( INCREMENT_PARAM, "50" );
		props.put( INITIAL_PARAM, "1" );

		// Unless explicitly overruled, we use a pooled optimizer
		props.put( OPT_PARAM, OptimizerFactory.StandardOptimizerDescriptor.POOLED.getExternalName() );

		// Extend with params
		if ( params.containsKey( OPT_PARAM ) ) {
			props.put( OPT_PARAM, params.getProperty( OPT_PARAM ) );
		}

		if ( params.contains( "initialValue" ) ) {
			props.put( INITIAL_PARAM, String.valueOf( Integer.valueOf( params.getProperty( "initialValue" ) ) + 1 ) );
		}

		if ( params.contains( "allocationSize" ) ) {
			props.put( INCREMENT_PARAM, String.valueOf( Integer.valueOf( params.getProperty( "allocationSize" ) ) ) );
		}

		if ( params.contains( "supportPredefinedIds" ) ) {
			supportPredefinedIds = Boolean.valueOf( params.getProperty( "supportPredefinedIds" ) );
		}

		super.configure( type, props, dialect );
	}

	@Override
	public Serializable generate( SessionImplementor session, Object object ) {
		Serializable id = session.getEntityPersister( entityName, object )
		                         .getClassMetadata().getIdentifier( object, session );

		if ( supportPredefinedIds && id != null && !new Long( 0 ).equals( id ) ) {
			return id;
		}

		return super.generate( session, object );
	}
}
