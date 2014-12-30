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
package com.foreach.across.modules.logging.services;

import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.DatabaseStrategy;
import com.foreach.across.modules.logging.business.FunctionalLogEvent;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.LogEventDto;
import com.foreach.across.modules.logging.repositories.FunctionalLogEventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunctionalLogDBService implements LogDelegateService
{
	@Autowired
	private FunctionalLogEventRepository functionalLogEventRepository;

	@Autowired
	private LoggingModuleSettings loggingModuleSettings;

	@Override
	public boolean supports( LogType logType ) {
		return logType == LogType.FUNCTIONAL &&
				( loggingModuleSettings.getFunctionalDBStrategy() == DatabaseStrategy.ROLLING ||
						loggingModuleSettings.getFunctionalDBStrategy() == DatabaseStrategy.SINGLE_TABLE );
	}

	@Override
	public void log( LogEventDto dto ) {
		FunctionalLogEvent entity;

		try {
			entity = FunctionalLogEvent.class.newInstance();
		}
		catch ( InstantiationException | IllegalAccessException e ) {
			throw new RuntimeException( e );
		}

		BeanUtils.copyProperties( dto, entity );
		functionalLogEventRepository.create( entity );
	}
}
