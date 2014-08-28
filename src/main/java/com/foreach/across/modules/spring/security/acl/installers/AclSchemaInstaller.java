package com.foreach.across.modules.spring.security.acl.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Installs the ACL database schema", version = 2)
public class AclSchemaInstaller extends AcrossLiquibaseInstaller
{
}
