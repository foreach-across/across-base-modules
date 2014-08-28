package com.foreach.across.modules.ehcache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Dummy configuration class that enables caching in any ApplicationContext where it is loaded.
 */
@Configuration
@EnableCaching(order = EhcacheClientModuleConfig.INTERCEPT_ORDER)
public class EhcacheClientModuleConfig
{
	/**
	 * Order for the AOP interceptor.
	 */
	public static final int INTERCEPT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;
}
