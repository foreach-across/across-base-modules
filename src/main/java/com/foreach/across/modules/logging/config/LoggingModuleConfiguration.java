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

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossException;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.DatabaseStrategy;
import com.foreach.across.modules.logging.business.LogType;
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
	private AcrossContext acrossContext;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Autowired
	private LoggingModuleSettings loggingModuleSettings;

	@PostConstruct
	public void checkSettingsValidity() {
		if ( loggingModuleSettings.getFunctionalDBStrategy() == DatabaseStrategy.ROLLING ) {
			validateRollingSchedule( loggingModuleSettings.getFunctionalDBRollingSchedule(), LogType.FUNCTIONAL );
			validateRollingTimeSpan( loggingModuleSettings.getFunctionalDBRollingTimeSpan(), LogType.FUNCTIONAL );
		}

		if ( loggingModuleSettings.getTechnicalDBStrategy() == DatabaseStrategy.ROLLING ) {
			validateRollingSchedule( loggingModuleSettings.getTechnicalDBRollingSchedule(), LogType.TECHNICAL );
			validateRollingTimeSpan( loggingModuleSettings.getTechnicalDBRollingTimeSpan(), LogType.TECHNICAL );
		}

		if ( acrossContext.getModule( AcrossHibernateModule.NAME ) == null ) {
			if ( loggingModuleSettings.getFunctionalDBStrategy() != DatabaseStrategy.NONE ||
					loggingModuleSettings.getTechnicalDBStrategy() != DatabaseStrategy.NONE ) {
				throw new AcrossException(
						"The current settings for the LoggingModule rely on database, yet the AcrossHibernateModule is not enabled." );
			}
		}
	}

	private void validateRollingSchedule( String rollingSchedule, LogType logType ) {
		try {
			new CronSequenceGenerator( rollingSchedule );
		}
		catch ( IllegalArgumentException e ) {
			String errorMsg = String.format( "%s DB Rolling Schedule specified was not valid : \"%s\".", logType,
			                                 rollingSchedule );
			throw new AcrossException( errorMsg, e );
		}
	}

	private void validateRollingTimeSpan( String rollingTimeSpan, LogType logType ) {
		try {
			DatatypeFactory.newInstance().newDuration( rollingTimeSpan );
		}
		catch ( Exception e ) {
			String errorMsg = String.format( "%s DB Timespan specified was not valid : \"%s\".", logType,
			                                 rollingTimeSpan );
			throw new AcrossException( errorMsg, e );
		}
	}
}
