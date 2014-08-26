package com.foreach.across.modules.spring.security.services;

import com.foreach.across.modules.spring.security.business.SecurityPrincipalSid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
public class SecurityPrincipalJdbcAclService extends JdbcMutableAclService
{
	public SecurityPrincipalJdbcAclService( DataSource dataSource,
	                                        LookupStrategy lookupStrategy,
	                                        AclCache aclCache ) {
		super( dataSource, lookupStrategy, aclCache );
	}
}
