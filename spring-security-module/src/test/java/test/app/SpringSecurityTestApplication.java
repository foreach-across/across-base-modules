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

package test.app;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.AcrossWebModule;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * @author Steven Gentens
 * @since 3.0.0-SNAPSHOT
 */
@AcrossApplication(modules = { AcrossWebModule.NAME, SpringSecurityModule.NAME })
public class SpringSecurityTestApplication
{
	@Bean
	public DataSource acrossDataSource() {
		return new EmbeddedDatabaseBuilder().setType( EmbeddedDatabaseType.H2 ).build();
	}

	public static void main( String args[] ) {
		SpringApplication.run( SpringSecurityTestApplication.class, args );
	}
}
