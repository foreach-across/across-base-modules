package com.foreach.across.modules.user.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.generators.EntityIdGenerator;
import com.foreach.across.modules.entity.generators.label.PropertyLabelGenerator;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@AcrossDepends(required = "EntityModule")
@Configuration
public class UserEntitiesConfiguration implements EntityConfigurer
{
	@Override
	public boolean accepts( Class<?> entityClass ) {
		return entityClass.equals( Role.class ) || entityClass.equals( Permission.class );
	}

	@Override
	public void configure( EntityConfiguration configuration ) {
		configuration.setLabelGenerator( PropertyLabelGenerator.forProperty( configuration.getEntityType(),
		                                                                     "description" ) );

		configuration.setIdGenerator( new EntityIdGenerator()
		{
			@Override
			public Serializable getId( Object entity ) {
				if ( entity instanceof Role ) {
					return ( (Role) entity ).getName();
				}
				else if ( entity instanceof Permission ) {
					return ( (Permission) entity ).getName();
				}

				return null;
			}
		} );
	}
}
