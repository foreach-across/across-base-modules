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
package com.foreach.across.modules.entity.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface supporting fetching data for a generic {@link com.foreach.across.modules.entity.query.EntityQuery}
 * instance, used as abstraction for JPA, QueryDsl repositories.
 *
 * @author Arne Vandamme
 */
public interface EntityQueryPageFetcher
{
	Page fetchPage( EntityQuery query, Pageable pageable );
}
