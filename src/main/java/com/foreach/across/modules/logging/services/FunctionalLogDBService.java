package com.foreach.across.modules.logging.services;

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.logging.business.FunctionalLogEvent;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.FunctionalLogEventDto;
import com.foreach.across.modules.logging.dto.LogEventDto;
import com.foreach.across.modules.logging.repositories.FunctionalLogEventRepository;
import com.foreach.across.modules.logging.repositories.LogEventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class FunctionalLogDBService implements LogDelegateService
{
	@Autowired
	private FunctionalLogEventRepository functionalLogEventRepository;

	@Override
	public boolean supports( LogType logType ) {
		return logType == LogType.FUNCTIONAL;
	}

	@Override
	public void log( LogEventDto dto ) {
		FunctionalLogEvent entity;

		if ( !dto.isNewEntity() ) {
			entity = functionalLogEventRepository.getById( dto.getId() );

			if ( entity == null ) {
				throw new EntityNotFoundException( String.format( "No %s with id %s", FunctionalLogEvent.class.getSimpleName(),
				                                                  dto.getId() ) );
			}
		}
		else {
			try {
				entity = FunctionalLogEvent.class.newInstance();
			}
			catch ( InstantiationException | IllegalAccessException e ) {
				throw new RuntimeException( e );
			}
		}

		BeanUtils.copyProperties( dto, entity );

		if ( dto.isNewEntity() ) {
			functionalLogEventRepository.create( entity );
		}
		else {
			functionalLogEventRepository.update( entity );
		}

		dto.copyFrom( entity );
	}
}
