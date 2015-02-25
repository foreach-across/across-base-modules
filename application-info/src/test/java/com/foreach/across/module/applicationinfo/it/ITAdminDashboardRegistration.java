package com.foreach.across.module.applicationinfo.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.installers.InstallerAction;
import com.foreach.across.module.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author niels
 * @since 25/02/2015
 */
public class ITAdminDashboardRegistration
{
	@Test
	public void adminWebBeforeApplicationInfo() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );
				acrossContext.addModule( new AdminWebModule() );
				acrossContext.addModule( new ApplicationInfoModule() );
				acrossContext.addModule( new SpringSecurityModule() );
			}
		};

		assertDashboardPath( "/", configurer );
	}

	@Test
	public void adminWebAfterApplicationInfo() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );
				acrossContext.addModule( new ApplicationInfoModule() );
				acrossContext.addModule( new AdminWebModule() );
				acrossContext.addModule( new SpringSecurityModule() );
			}
		};

		assertDashboardPath( "/", configurer );
	}

	@Test
	public void adminWebCustomDashboardOnParent() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );
				acrossContext.setProperty( AdminWebModuleSettings.DASHBOARD_PATH, "/custom" );
				acrossContext.addModule( new ApplicationInfoModule() );
				acrossContext.addModule( new AdminWebModule() );
				acrossContext.addModule( new SpringSecurityModule() );
			}
		};

		assertDashboardPath( "/custom", configurer );
	}

	@Test
	public void adminWebCustomDashboardOnAdminWebModule() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );

				AdminWebModule AdminWebModule = new AdminWebModule();
				AdminWebModule.setProperty( AdminWebModuleSettings.DASHBOARD_PATH, "/other/custom" );
				acrossContext.addModule( AdminWebModule );
				acrossContext.addModule( new SpringSecurityModule() );

				acrossContext.addModule( new ApplicationInfoModule() );

			}
		};

		assertDashboardPath( "/other/custom", configurer );
	}

	private void assertDashboardPath( String expectedPath, AcrossContextConfigurer configurer ) {
		try (AcrossTestWebContext ctx = new AcrossTestWebContext( configurer )) {
			AdminWebModuleSettings settings = ctx.beanRegistry().getBeanOfTypeFromModule( "AdminWebModule",
			                                                                              AdminWebModuleSettings.class );

			assertEquals( expectedPath, settings.getDashboardPath() );
		}
	}
}
