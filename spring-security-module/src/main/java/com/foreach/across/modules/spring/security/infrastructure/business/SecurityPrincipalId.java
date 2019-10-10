package com.foreach.across.modules.spring.security.infrastructure.business;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class SecurityPrincipalId implements Serializable
{
	private final String id;

	public String toString() {
		return id;
	}

	public static SecurityPrincipalId of( String id ) {
		if ( StringUtils.isEmpty( id ) ) {
			throw new IllegalArgumentException( "A principal id may not be empty or null." );
		}
		return new SecurityPrincipalId( id );
	}
}
