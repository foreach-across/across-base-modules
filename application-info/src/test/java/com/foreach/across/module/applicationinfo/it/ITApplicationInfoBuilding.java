package com.foreach.across.module.applicationinfo.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.module.applicationinfo.ApplicationInfoModule;
import com.foreach.across.module.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.module.applicationinfo.business.AcrossApplicationInfo;
import com.foreach.across.test.AcrossTestContext;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ITApplicationInfoBuilding
{
	@Test
	public void noPropertiesConfigured() {
		long startOfTest = System.currentTimeMillis();

		try (AcrossTestContext ctx = new AcrossTestContext( new ModuleConfig() )) {
			AcrossApplicationInfo applicationInfo = ctx.beanRegistry().getBeanOfType( AcrossApplicationInfo.class );

			assertNotNull( applicationInfo );
			assertEquals( "unknown", applicationInfo.getApplicationId() );
			assertEquals( "unknown", applicationInfo.getApplicationName() );
			assertEquals( "unknown", applicationInfo.getEnvironmentId() );
			assertEquals( "unknown", applicationInfo.getEnvironmentName() );

			assertEquals( "unknown", applicationInfo.getBuildId() );
			assertNull( applicationInfo.getBuildDate() );

			assertNotNull( applicationInfo.getHostName() );
			assertEquals(
					String.format(
							"%s-%s-%s",
							applicationInfo.getApplicationId(),
							applicationInfo.getEnvironmentId(),
							applicationInfo.getHostName()
					),
					applicationInfo.getInstanceId()
			);

			assertNotNull( applicationInfo.getStartupDate() );
			assertNotNull( applicationInfo.getBootstrapStartDate() );
			assertNotNull( applicationInfo.getBootstrapEndDate() );

			assertTrue( applicationInfo.getStartupDate().getTime() >= startOfTest );
			assertTrue(
					applicationInfo.getBootstrapStartDate().getTime() >= applicationInfo.getStartupDate().getTime() );
			assertTrue( applicationInfo.getBootstrapEndDate().getTime() >= applicationInfo.getBootstrapStartDate()
			                                                                              .getTime() );
		}
	}

	@Test
	public void propertiesSet() {
		long startOfTest = System.currentTimeMillis();

		try (AcrossTestContext ctx = new AcrossTestContext( new ModuleConfig(), new PropertiesConfig() )) {
			AcrossApplicationInfo applicationInfo = ctx.beanRegistry().getBeanOfType( AcrossApplicationInfo.class );

			assertNotNull( applicationInfo );
			assertEquals( "myapp", applicationInfo.getApplicationId() );
			assertEquals( "My Application", applicationInfo.getApplicationName() );
			assertEquals( "test", applicationInfo.getEnvironmentId() );
			assertEquals( "Test Environment", applicationInfo.getEnvironmentName() );
			assertEquals( "someserver.test.com", applicationInfo.getHostName() );

			assertEquals( "release-1", applicationInfo.getBuildId() );
			assertNotNull( applicationInfo.getBuildDate() );
			assertTrue( applicationInfo.getBuildDate().getTime() >= startOfTest );

			assertNotNull( applicationInfo.getHostName() );
			assertEquals( "myapp-test-someserver.test.com", applicationInfo.getInstanceId() );

			assertNotNull( applicationInfo.getStartupDate() );
			assertNotNull( applicationInfo.getBootstrapStartDate() );
			assertNotNull( applicationInfo.getBootstrapEndDate() );

			assertEquals( "2014-10-15 13:30:15",
			              FastDateFormat.getInstance( "yyyy-MM-dd HH:mm:ss" )
			                            .format( applicationInfo.getStartupDate() )
			);

			assertTrue( applicationInfo.getBootstrapStartDate().after( new Date( startOfTest ) ) );
			assertTrue( applicationInfo.getBootstrapEndDate().after( applicationInfo.getBootstrapStartDate() ) );
		}
	}

	protected static class PropertiesConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.setProperty( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" );
			context.setProperty( ApplicationInfoModuleSettings.APPLICATION_NAME, "My Application" );
			context.setProperty( ApplicationInfoModuleSettings.ENVIRONMENT_ID, "test" );
			context.setProperty( ApplicationInfoModuleSettings.ENVIRONMENT_NAME, "Test Environment" );
			context.setProperty( ApplicationInfoModuleSettings.HOSTNAME, "someserver.test.com" );
			context.setProperty( ApplicationInfoModuleSettings.BUILD_ID, "release-1" );
			context.setProperty( ApplicationInfoModuleSettings.BUILD_DATE, new Date() );
			context.setProperty( ApplicationInfoModuleSettings.STARTUP_DATE, "2014-10-15 13:30:15" );

		}
	}

	protected static class ModuleConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new ApplicationInfoModule() );
		}
	}
}
