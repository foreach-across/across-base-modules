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
package com.foreach.across.modules.logging.config;

import com.foreach.across.core.AcrossException;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.DatabaseStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.support.CronSequenceGenerator;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeFactory;

/**
 * @author Andy Somers
 */
@Configuration
@ComponentScan("com.foreach.across.modules.logging.controllers")
public class LoggingModuleConfiguration
{
	@Autowired
	private LoggingModuleSettings loggingModuleSettings;

	@PostConstruct
	public void checkSettingsValidity() {
		if ( loggingModuleSettings.getFunctionalDBStrategy() == DatabaseStrategy.ROLLING ) {
			try {
				CronSequenceGenerator cronSeq = new CronSequenceGenerator(
						loggingModuleSettings.getFunctionalDBRollingSchedule() );
			}
			catch ( IllegalArgumentException e ) {
				throw new AcrossException( "Functional DB Rolling Schedule specified was not valid.", e );
			}
			try {
				DatatypeFactory.newInstance().newDuration( loggingModuleSettings.getFunctionalDBRollingTimeSpan() );
			}
			catch ( Exception e ) {
				throw new AcrossException( "Oops", e );
			}
			//Test Schedule and TimeSpan
		}
		if ( loggingModuleSettings.getTechnicalDBStrategy() == DatabaseStrategy.ROLLING ) {
			try {
				CronSequenceGenerator cronSeq = new CronSequenceGenerator(
						loggingModuleSettings.getTechnicalDBRollingSchedule() );
			}
			catch ( IllegalArgumentException e ) {
				throw new AcrossException( "Technical DB Rolling Schedule specified was not valid.", e );
			}
			try {
				DatatypeFactory.newInstance().newDuration( loggingModuleSettings.getTechnicalDBRollingTimeSpan() );
			}
			catch ( Exception e ) {
				throw new AcrossException( "Oops", e );
			}
			//Test Schedule and TimeSpan
		}
	}
}
