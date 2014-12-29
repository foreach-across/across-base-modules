package com.foreach.across.modules.logging.installers;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

@Installer(description = "Installs database schema for logging events", version = 1)
@AcrossDepends(required = "AcrossHibernateModule")
public class LoggingModuleSchemaInstaller extends AcrossLiquibaseInstaller
{

	public LoggingModuleSchemaInstaller() {
		super();
	}
}
