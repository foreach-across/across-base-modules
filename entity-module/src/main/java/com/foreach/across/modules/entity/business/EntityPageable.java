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
package com.foreach.across.modules.entity.business;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author Arne Vandamme
 */
public class EntityPageable
{
	/**
	 * Special implementation that does not allow any properties to be modified and implements
	 * equals as reference equals.
	 */
	public static final Pageable DEFAULT = new Pageable()
	{
		@Override
		public int getPageNumber() {
			return 0;
		}

		@Override
		public int getPageSize() {
			return 10;
		}

		@Override
		public int getOffset() {
			return 0;
		}

		@Override
		public Sort getSort() {
			return null;
		}

		@Override
		public Pageable next() {
			return new PageRequest( 1, getPageSize(), null );
		}

		@Override
		public Pageable previousOrFirst() {
			return this;
		}

		@Override
		public Pageable first() {
			return this;
		}

		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public boolean equals( Object obj ) {
			return obj == this;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	};

	private EntityPageable(){
	}

	public static boolean isNullOrDefault( Pageable pageable ) {
		return pageable != null && pageable.equals( DEFAULT );
	}
}
