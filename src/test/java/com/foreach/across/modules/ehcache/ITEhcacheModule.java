package com.foreach.across.modules.ehcache;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.AcrossSpringApplicationContext;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossTestContextConfigurer;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITEhcacheModule.Config.class)
public class ITEhcacheModule {

	@Autowired
	private net.sf.ehcache.CacheManager cacheManager;

	@Test
	public void bootstrapModule() {
		assertNotNull( cacheManager );
		assertEquals( Status.STATUS_ALIVE, cacheManager.getStatus() );

		String[] cacheNames = cacheManager.getCacheNames();
		assertNotNull( cacheNames );
		assertEquals( 1, cacheNames.length );
		assertEquals( "oneElementCache", cacheNames[0] );

		Cache cache = cacheManager.getCache( "oneElementCache" );
		assertNotNull( cache );

		cache.put( new Element( "item1", "value1" ) );
		assertNotNull( cache.get( "item1" ) );

		cache.put( new Element( "item2", "value2" ) );
		assertNull( cache.get( "item1" ) );
		assertNotNull( cache.get( "item2" ) );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossTestContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			ApplicationContext applicationContext = context.getParentApplicationContext();
			if( !(applicationContext instanceof AcrossSpringApplicationContext ) ) {
				EhcacheModule ehcacheModule = new EhcacheModule();
				ehcacheModule.setConfigLocation( new ClassPathResource( "test-ehcache.xml" ) );
				context.addModule( ehcacheModule );
			}
		}
	}
}