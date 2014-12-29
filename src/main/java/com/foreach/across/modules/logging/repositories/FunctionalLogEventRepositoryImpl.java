package com.foreach.across.modules.logging.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.logging.business.FunctionalLogEvent;
import org.springframework.stereotype.Repository;

@Repository
public class FunctionalLogEventRepositoryImpl extends BasicRepositoryImpl<FunctionalLogEvent> implements FunctionalLogEventRepository
{
}
