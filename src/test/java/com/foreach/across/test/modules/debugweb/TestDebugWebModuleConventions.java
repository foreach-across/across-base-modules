package com.foreach.across.test.modules.debugweb;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestDebugWebModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return true;
	}

	@Override
	protected AcrossModule createModule() {
		return new DebugWebModule();
	}
}
