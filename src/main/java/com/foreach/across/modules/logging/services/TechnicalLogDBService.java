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

import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.business.TechnicalLogEvent;
import com.foreach.across.modules.logging.dto.LogEventDto;
import com.foreach.across.modules.logging.repositories.TechnicalLogEventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TechnicalLogDBService implements LogDelegateService
{
	@Autowired
	private TechnicalLogEventRepository technicalLogEventRepository;

	@Override
	public boolean supports( LogType logType ) {
		return logType == LogType.TECHNICAL;
	}

	@Override
	public void log( LogEventDto dto ) {
		TechnicalLogEvent entity;

		try {
			entity = TechnicalLogEvent.class.newInstance();
		}
		catch ( InstantiationException | IllegalAccessException e ) {
			throw new RuntimeException( e );
		}

		BeanUtils.copyProperties( dto, entity );
		technicalLogEventRepository.create( entity );
	}
}
