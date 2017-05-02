/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.hibernate;

import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.hibernate.installers.AuditableSchemaInstaller;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Marc Vanbrabant
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration
public class TestAuditableSchemaInstaller
{
	@Autowired
	private DataSource dataSource;

	@Test
	public void auditableInstallerRunsOnAcrossModulesTable() throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );
		List<String> items = jdbcTemplate.query( "SELECT * FROM ACROSSMODULES", ( rs, rowNum ) -> {
			assertEquals( null, rs.getString( "created_by" ) );
			assertEquals( null, rs.getDate( "created_date" ) );
			assertEquals( null, rs.getString( "last_modified_by" ) );
			assertEquals( null, rs.getDate( "last_modified_date" ) );
			return rs.getString( "module_id" );
		} );
		assertNotNull( items );
		assertTrue( items.contains( "Across" ) );
		assertTrue( items.contains( "AcrossHibernateModule" ) );
	}

	@Configuration
	@AcrossTestConfiguration(modules = AcrossHibernateModule.NAME)
	protected static class Config
	{
		@Event
		public void afterBootstratp( AcrossModuleBeforeBootstrapEvent event ) {
			event.getModule().getBootstrapConfiguration().getInstallers().add( CustomAuditableInstaller.class );
		}
	}

	@Order(2)
	@Installer(description = "Adds auditable columns to all entities", version = 1, phase = InstallerPhase.BeforeModuleBootstrap)
	public static class CustomAuditableInstaller extends AuditableSchemaInstaller
	{
		public CustomAuditableInstaller() {
			setDefaultSchema( "PUBLIC" );
		}

		@Override
		protected Collection<String> getTableNames() {
			return Collections.singletonList( "ACROSSMODULES" );
		}
	}
}
