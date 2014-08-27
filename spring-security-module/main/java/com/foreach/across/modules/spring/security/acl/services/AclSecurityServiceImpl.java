package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.business.SecurityPrincipalSid;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalHierarchy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @author Arne Vandamme
 */
@Service
public class AclSecurityServiceImpl implements AclSecurityService
{
	@Autowired
	private MutableAclService fetchedAclService;

	@Autowired
	private PermissionEvaluator fetchedAclPermissionEvaluator;

	@Transactional(readOnly = true)
	@Override
	public MutableAcl getAcl( IdBasedEntity entity ) {
		try {
			return (MutableAcl) aclService().readAclById( identity( entity ) );
		}
		catch ( NotFoundException nfe ) {
			return null;
		}
	}

	@Transactional
	@Override
	public MutableAcl createAcl( IdBasedEntity entity ) {
		return createAclWithParent( entity, null );
	}

	@Transactional
	@Override
	public MutableAcl createAclWithParent( IdBasedEntity entity, IdBasedEntity parent ) {
		MutableAclService aclService = aclService();

		ObjectIdentity oi = identity( entity );

		MutableAcl acl;

		boolean update = false;

		try {
			acl = (MutableAcl) aclService.readAclById( oi );
		}
		catch ( NotFoundException nfe ) {
			acl = aclService.createAcl( oi );
			update = true;
		}

		if ( parent != null && !parent.equals( acl.getParentAcl() ) ) {
			Acl parentAcl = aclService.readAclById( identity( parent ) );
			acl.setParent( parentAcl );
			update = true;
		}

		return update ? aclService.updateAcl( acl ) : acl;
	}

	@Transactional
	@Override
	public void allow( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... permissions ) {
		updateAces( sid( principal ), entity, true, permissions );
	}

	@Transactional
	@Override
	public void allow( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( principal ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( principal ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void allow( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void allow( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authentication ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authentication ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authentication ), entity, false, aclPermissions );
	}

	private void updateAces( Sid sid, IdBasedEntity entity, Boolean grantAction, AclPermission... aclPermissions ) {
		MutableAclService service = aclService();

		boolean shouldRevoke = grantAction == null;
		ObjectIdentity objectIdentity = identity( entity );

		MutableAcl acl;

		try {
			acl = (MutableAcl) service.readAclById( objectIdentity );
		}
		catch ( NotFoundException nfe ) {
			acl = fetchedAclService.createAcl( objectIdentity );
		}

		List<AccessControlEntry> aces = acl.getEntries();

		for ( AclPermission aclPermission : aclPermissions ) {
			int index = aces.size();
			AccessControlEntry ace = findAce( aces, sid, aclPermission );

			if ( ace != null && ( shouldRevoke || ace.isGranting() != grantAction ) ) {
				index = aces.indexOf( ace );
				acl.deleteAce( index );

				ace = null;
			}

			if ( ace == null && !shouldRevoke ) {
				acl.insertAce( index, aclPermission, sid, grantAction );
			}
		}

		fetchedAclService.updateAcl( acl );
	}

	private AccessControlEntry findAce( List<AccessControlEntry> aces, Sid sid, AclPermission permission ) {
		for ( AccessControlEntry ace : aces ) {
			if ( ace.getSid().equals( sid ) && ace.getPermission().equals( permission ) ) {
				return ace;
			}
		}
		return null;
	}

	@Transactional
	@Override
	public void deleteAcl( IdBasedEntity entity, boolean deleteChildren ) {
		aclService().deleteAcl( identity( entity ), deleteChildren );
	}

	@Transactional
	@Override
	public MutableAcl updateAcl( MutableAcl acl ) {
		return aclService().updateAcl( acl );
	}

	@Transactional
	@Override
	public void changeAclOwner( MutableAcl acl, SecurityPrincipal principal ) {
		acl.setOwner( sid( principal ) );
		updateAcl( acl );
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermission( Authentication authentication, IdBasedEntity entity, AclPermission permission ) {
		return aclPermissionEvaluator().hasPermission( authentication, entity, permission );
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermission( SecurityPrincipal principal, IdBasedEntity entity, AclPermission permission ) {
		List<Sid> sids = buildSids( principal );
		List<org.springframework.security.acls.model.Permission> aclPermissions =
				Collections.<org.springframework.security.acls.model.Permission>singletonList( permission );

		try {
			// Lookup only ACLs for SIDs we're interested in
			Acl acl = aclService().readAclById( identity( entity ), sids );

			if ( acl.isGranted( aclPermissions, sids, false ) ) {
				return true;
			}
		}
		catch ( NotFoundException nfe ) {
			return false;
		}

		return false;
	}

	private List<Sid> buildSids( SecurityPrincipal principal ) {
		Collection<SecurityPrincipal> principals = new LinkedList<>();
		principals.add( principal );

		if ( principal instanceof SecurityPrincipalHierarchy ) {
			principals.addAll( ( (SecurityPrincipalHierarchy) principal ).getParentPrincipals() );
		}

		List<Sid> sids = new ArrayList<>();
		Collection<Sid> authoritySids = new LinkedHashSet<>();

		for ( SecurityPrincipal candidate : principals ) {
			sids.add( new SecurityPrincipalSid( candidate ) );

			for ( GrantedAuthority authority : candidate.getAuthorities() ) {
				authoritySids.add( new GrantedAuthoritySid( authority ) );
			}
		}

		sids.addAll( authoritySids );

		return sids;
	}

	private Sid sid( Authentication authentication ) {
		return new PrincipalSid( authentication );
	}

	private Sid sid( String authority ) {
		return new GrantedAuthoritySid( authority );
	}

	private Sid sid( GrantedAuthority authority ) {
		return new GrantedAuthoritySid( authority );
	}

	private Sid sid( SecurityPrincipal principal ) {
		return new SecurityPrincipalSid( principal );
	}

	private ObjectIdentity identity( IdBasedEntity entity ) {
		return new ObjectIdentityImpl( ClassUtils.getUserClass( entity.getClass() ), entity.getId() );
	}

	private MutableAclService aclService() {
		return fetchedAclService;
	}

	private PermissionEvaluator aclPermissionEvaluator() {
		return fetchedAclPermissionEvaluator;
	}
}
