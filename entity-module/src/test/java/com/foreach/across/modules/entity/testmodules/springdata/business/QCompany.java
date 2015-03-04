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
package com.foreach.across.modules.entity.testmodules.springdata.business;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

/**
 * Manually created dummy QueryDSL Q class.
 * @author Arne Vandamme
 */
public class QCompany extends EntityPathBase<Company>
{
	private static final long serialVersionUID = 475764283L;

	public static final QCompany company = new QCompany( "company" );

	public QCompany( String variable ) {
		super( Company.class, forVariable( variable ) );
	}

	public QCompany( Path<? extends Company> path ) {
		super( path.getType(), path.getMetadata() );
	}

	public QCompany( PathMetadata<?> metadata ) {
		super( Company.class, metadata );
	}
}
