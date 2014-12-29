package com.foreach.across.modules.it.logging;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.logging.services.FunctionalLogDBService;
import com.foreach.across.modules.logging.services.LoggingService;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITLoggingModule.Config.class)
public class ITLoggingModule
{
	@Autowired
	private LoggingService loggingService;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Test
	public void verifyBootstrapped() {
		assertNotNull(loggingService);

		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( LoggingModule.NAME );

		try {
			assertNull( moduleInfo.getApplicationContext().getBean( FunctionalLogDBService.class ) );
		}
		catch ( NoSuchBeanDefinitionException e ) {
			assertTrue( true ); //If we get this exception, the desired result has been achieved.
		}
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossWebModule() );
			context.addModule( new LoggingModule() );
		}
	}
}
