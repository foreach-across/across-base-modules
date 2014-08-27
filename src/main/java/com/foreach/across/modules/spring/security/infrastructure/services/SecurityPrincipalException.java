package com.foreach.across.modules.spring.security.infrastructure.services;

/**
 * @author Arne Vandamme
 */
public class SecurityPrincipalException extends RuntimeException
{
	public SecurityPrincipalException() {
		super();
	}

	public SecurityPrincipalException( String message ) {
		super( message );
	}

	public SecurityPrincipalException( String message, Throwable cause ) {
		super( message, cause );
	}

	public SecurityPrincipalException( Throwable cause ) {
		super( cause );
	}

	protected SecurityPrincipalException( String message,
	                                      Throwable cause,
	                                      boolean enableSuppression,
	                                      boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}
}
