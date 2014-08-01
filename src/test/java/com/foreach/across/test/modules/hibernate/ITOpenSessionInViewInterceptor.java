package com.foreach.across.test.modules.hibernate;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITOpenSessionInViewInterceptor.Config.class)
public class ITOpenSessionInViewInterceptor
{
	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Test
	public void verifyInterceptorCreated() {
		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( AcrossHibernateModule.NAME );

		assertNotNull( moduleInfo.getApplicationContext().getBean( OpenSessionInViewInterceptor.class ) );
	}

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( acrossHibernateModule() );
		}

		private AcrossHibernateModule acrossHibernateModule() {
			AcrossHibernateModule acrossHibernateModule = new AcrossHibernateModule();
			acrossHibernateModule.setProperty( AcrossHibernateModuleSettings.OPEN_SESSION_IN_VIEW_INTERCEPTOR,
			                                   true );

			return acrossHibernateModule;
		}
	}
}
