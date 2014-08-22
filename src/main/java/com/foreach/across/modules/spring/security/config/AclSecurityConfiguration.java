package com.foreach.across.modules.spring.security.config;

import com.foreach.across.core.database.DatabaseInfo;
import com.foreach.across.modules.spring.security.services.SecurityPrincipalJdbcAclService;
import com.foreach.across.modules.spring.security.strategy.SecurityPrincipalAclAuthorizationStrategy;
import com.foreach.across.modules.spring.security.strategy.SecurityPrincipalSidRetrievalStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Configuration
public class AclSecurityConfiguration
{
	public static final String CACHE_NAME = "securityAclCache";

	private static final Logger LOG = LoggerFactory.getLogger( AclSecurityConfiguration.class );

	@Autowired
	private DataSource dataSource;

	@Autowired(required = false)
	private CacheManager cacheManager;

	@Bean
	public AclPermissionEvaluator aclPermissionEvaluator() {
		AclPermissionEvaluator evaluator = new AclPermissionEvaluator( aclService() );
		evaluator.setSidRetrievalStrategy( new SecurityPrincipalSidRetrievalStrategy() );

		return evaluator;
	}

	@Bean
	public MutableAclService aclService() {
		JdbcMutableAclService aclService = new SecurityPrincipalJdbcAclService( dataSource, lookupStrategy(),
		                                                                        aclCache() );

		DatabaseInfo databaseInfo = DatabaseInfo.retrieve( dataSource );

		if ( databaseInfo.isMySQL() ) {
			aclService.setClassIdentityQuery( "SELECT LAST_INSERT_ID()" );
			aclService.setSidIdentityQuery( "SELECT LAST_INSERT_ID()" );
		}
		else if ( databaseInfo.isSqlServer() ) {
			aclService.setClassIdentityQuery( "SELECT @@IDENTITY" );
			aclService.setSidIdentityQuery( "SELECT @@IDENTITY" );
		}
		else if ( databaseInfo.isOracle() ) {
			aclService.setClassIdentityQuery( "SELECT acl_class_sequence.currval FROM dual" );
			aclService.setSidIdentityQuery( "SELECT acl_sid_sequence.currval FROM dual" );
		}

		return aclService;
	}

	@Bean
	public AclCache aclCache() {
		return new SpringCacheBasedAclCache( cacheInstance(), permissionGrantingStrategy(),
		                                     aclAuthorizationStrategy() );
	}

	private Cache cacheInstance() {
		if ( cacheManager != null ) {
			return cacheManager.getCache( CACHE_NAME );
		}

		LOG.warn( "No cache suitable for ACL caching found - reverting to no caching.  If you want to use " +
				          "ACL in your application, a CacheManager providing cache named {} is highly advised " +
				          "for performance reasons.",
		          CACHE_NAME );

		return new Cache()
		{
			@Override
			public String getName() {
				return "noop";
			}

			@Override
			public Object getNativeCache() {
				return null;
			}

			@Override
			public ValueWrapper get( Object key ) {
				return null;
			}

			@Override
			public <T> T get( Object key, Class<T> type ) {
				return null;
			}

			@Override
			public void put( Object key, Object value ) {

			}

			@Override
			public void evict( Object key ) {

			}

			@Override
			public void clear() {

			}
		};
	}

	@Bean
	public LookupStrategy lookupStrategy() {
		return new BasicLookupStrategy( dataSource, aclCache(), aclAuthorizationStrategy(),
		                                permissionGrantingStrategy() );
	}

	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		SecurityPrincipalAclAuthorizationStrategy strategy = new SecurityPrincipalAclAuthorizationStrategy(
				new SimpleGrantedAuthority( "manage user roles" )
		);
		strategy.setSidRetrievalStrategy( sidRetrievalStrategy() );

		return strategy;
	}

	@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy() {
		return new DefaultPermissionGrantingStrategy( new ConsoleAuditLogger() );
	}

	@Bean
	public SidRetrievalStrategy sidRetrievalStrategy() {
		return new SecurityPrincipalSidRetrievalStrategy();
	}
}
