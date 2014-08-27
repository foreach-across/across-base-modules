package com.foreach.across.test.modules.spring.security.infrastructure;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestSpringSecurityInfrastructureModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return false;
	}

	@Override
	protected AcrossModule createModule() {
		return new SpringSecurityInfrastructureModule();
	}
}
