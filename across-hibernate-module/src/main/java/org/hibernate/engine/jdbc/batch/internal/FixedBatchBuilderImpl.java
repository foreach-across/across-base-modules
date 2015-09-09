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
package org.hibernate.engine.jdbc.batch.internal;

import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

public class FixedBatchBuilderImpl extends BatchBuilderImpl {

	private static int size = 0;

	@Override
    public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        return size > 1
                ? new BatchingBatch( key, jdbcCoordinator, size )
                : new FixedNonBatchingBatch( key, jdbcCoordinator );
    }

	public static void setSize( int batchSize ) {
		size = batchSize;
	}
}
