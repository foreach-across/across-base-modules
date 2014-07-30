package com.foreach.across.test.modules.spring.security;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestSpringSecurityModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return false;
	}

	@Override
	protected AcrossModule createModule() {
		return new SpringSecurityModule();
	}
}
