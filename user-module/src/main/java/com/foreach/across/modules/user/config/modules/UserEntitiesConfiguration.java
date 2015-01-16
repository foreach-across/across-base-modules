package com.foreach.across.modules.user.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.config.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "EntityModule")
@Configuration
public class UserEntitiesConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.entity( User.class )
					 /*.properties()
						.order( "id", "email", "displayName" )
						.property( "created", "Created", new AuditableCreatedPrinter() ).and()
						.property( "lastModified", "Last modified", new AuditableLastModifiedPrinter() ).and()
						.hide( "createdDate", "createdBy", "lastModifiedDate", "lastModifiedBy" )
						.and()*/
		             .view( "crud-list" )
		             .properties(
				             "id",
				             "email",
				             "displayName",
				             "group-membership",
				             "role-membership",
				             "createdDate",
				             "createdBy",
				             "lastModifiedDate",
				             "lastModifiedBy"
		             )
		             .property( "group-membership", "Groups", "groups.size()" )
		             .property( "role-membership", "Roles", "roles.size()" );

		configuration.entity( Role.class )
		             .properties()
		             .property( "name", "Key" )
		             .property( "description", "Name" )
		             .and()
		             .view( "crud-list" ).properties( "description", "name" );

	}
}
