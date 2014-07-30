package com.foreach.across.modules.ehcache;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestEhcacheModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return false;
	}

	@Override
	protected AcrossModule createModule() {
		return new EhcacheModule();
	}
}
