package com.foreach.across.test.modules.hibernate;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestAcrossHibernateModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return true;
	}

	@Override
	protected AcrossModule createModule() {
		return new AcrossHibernateModule();
	}
}
