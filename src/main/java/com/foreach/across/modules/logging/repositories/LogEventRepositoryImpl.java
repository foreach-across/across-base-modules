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
package com.foreach.across.modules.logging.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.logging.business.FunctionalLogEvent;
import com.foreach.across.modules.logging.business.LogEvent;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.business.TechnicalLogEvent;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class LogEventRepositoryImpl extends BasicRepositoryImpl<LogEvent> implements LogEventRepository
{
	@Override
	public Collection<LogEvent> getAll() {
		Criteria criteria = distinct();
		criteria = orderByTimeDesc( criteria );
		return criteria.list();
	}

	@Override
	public Collection<LogEvent> getAllOfType( LogType logType ) {
		Criteria criteria = distinct().add( Restrictions.eq( "class", getSpecificBusinessClass( logType ) ) );
		criteria = orderByTimeDesc( criteria );
		return criteria.list();
	}

	@Override
	public Collection<LogEvent> getAmountOfType( LogType logType, int numberOfLastResults ) {
		Criteria criteria = distinct().add( Restrictions.eq( "class", getSpecificBusinessClass( logType ) ) )
		                              .setMaxResults( numberOfLastResults );
		criteria = orderByTimeDesc( criteria );
		return criteria.list();
	}

	@Override
	public Collection<LogEvent> getAmount( int numberOfLastResults ) {
		Criteria criteria = distinct().setMaxResults( numberOfLastResults );
		criteria = orderByTimeDesc( criteria );
		return criteria.list();
	}

	private Criteria orderByTimeDesc( Criteria c ) {
		return c.addOrder( Order.desc( "time" ) );
	}

	private Class<? extends LogEvent> getSpecificBusinessClass( LogType logType ) {
		if ( logType == LogType.FUNCTIONAL ) {
			return FunctionalLogEvent.class;
		}
		else {
			return TechnicalLogEvent.class;
		}
	}
}
