package com.foreach.across.modules.spring.security.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Installs the ACL database schema", version = 1)
public class AclSchemaInstaller extends AcrossLiquibaseInstaller
{
}