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

package com.foreach.across.samples.logging.application.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

@Controller
public class TestLogController
{
	private Logger LOG = LoggerFactory.getLogger( TestLogController.class );

	@Autowired
	private DataSource dataSource;

	@ResponseBody
	@RequestMapping("/generate-error")
	public String generateError() {
		LOG.error( "Dummy error", new RuntimeException( "wOOO" ) );
		return "ERROR LOGGED";
	}

	@ResponseBody
	@RequestMapping("/test-hikari")
	public String doDatabaseActions() throws SQLException {
		try (Statement statement = dataSource.getConnection( "poc", "" ).createStatement()) {
			statement.execute( "SELECT 1" );
		}
		return "OK";
	}
}
