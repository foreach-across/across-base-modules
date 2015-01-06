package com.foreach.across.modules.oauth2.repositories;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2ClientRepository extends JpaRepository<OAuth2Client, Long>
{
	OAuth2Client findByClientId( String clientId );
}
