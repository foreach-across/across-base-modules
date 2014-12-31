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

import com.foreach.across.modules.logging.business.LogEvent;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.repositories.LogEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class LogDBReaderServiceImpl implements LogDBReaderService
{
	@Autowired
	private LogEventRepository logEventRepository;

	@Override
	@Transactional(readOnly = true)
	public Collection<LogEvent> getEvents() {
		return logEventRepository.getAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<LogEvent> getEvents( LogType logType ) {
		return logEventRepository.getAllOfType( logType );
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<LogEvent> getEvents( LogType logType, int numberOfLastResults ) {
		return logEventRepository.getAmountOfType( logType, numberOfLastResults );
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<LogEvent> getEvents( int numberOfLastResults ) {
		return logEventRepository.getAmount( numberOfLastResults );
	}
}
