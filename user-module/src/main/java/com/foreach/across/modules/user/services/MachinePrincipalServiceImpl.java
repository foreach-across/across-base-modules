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
package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.repositories.MachinePrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
@Service
public class MachinePrincipalServiceImpl implements MachinePrincipalService
{
	@Autowired
	private MachinePrincipalRepository machinePrincipalRepository;

	@Override
	public Collection<MachinePrincipal> getMachinePrincipals() {
		return machinePrincipalRepository.findAll( new Sort( Sort.Direction.ASC, "name" ) );
	}

	@Override
	public MachinePrincipal getMachinePrincipalById( long id ) {
		return machinePrincipalRepository.findOne( id );
	}

	@Override
	public MachinePrincipal getMachinePrincipalByName( String name ) {
		return machinePrincipalRepository.findByName( name );
	}

	@Override
	public MachinePrincipal save( MachinePrincipal machinePrincipalDto ) {
		return BasicServiceHelper.save( machinePrincipalDto, machinePrincipalRepository );
	}
}
