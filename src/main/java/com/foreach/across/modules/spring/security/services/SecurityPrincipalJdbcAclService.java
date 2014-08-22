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

	/**
	 * Custom implementation supporting the {@link com.foreach.across.modules.spring.security.business.SecurityPrincipalSid}.
	 */
	@Override
	public MutableAcl createAcl( ObjectIdentity objectIdentity ) throws AlreadyExistsException {
		Assert.notNull( objectIdentity, "Object Identity required" );

		// Check this object identity hasn't already been persisted
		if ( retrieveObjectIdentityPrimaryKey( objectIdentity ) != null ) {
			throw new AlreadyExistsException( "Object identity '" + objectIdentity + "' already exists" );
		}

		// Need to retrieve the current principal, in order to know who "owns" this ACL (can be changed later on)
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		PrincipalSid sid = SecurityPrincipalSid.forAuthentication( auth );

		// Create the acl_object_identity row
		createObjectIdentity( objectIdentity, sid );

		// Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
		Acl acl = readAclById( objectIdentity );
		Assert.isInstanceOf( MutableAcl.class, acl, "MutableAcl should be been returned" );

		return (MutableAcl) acl;
	}
}
