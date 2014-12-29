package com.foreach.across.modules.logging.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.logging.business.FunctionalLogEvent;
import com.foreach.across.modules.logging.business.LogEvent;

public interface LogEventRepository extends BasicRepository<LogEvent>
{
}
