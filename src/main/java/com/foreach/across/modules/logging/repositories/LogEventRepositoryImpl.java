package com.foreach.across.modules.logging.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.logging.business.LogEvent;
import org.springframework.stereotype.Repository;

@Repository
public class LogEventRepositoryImpl extends BasicRepositoryImpl<LogEvent> implements LogEventRepository
{
}
