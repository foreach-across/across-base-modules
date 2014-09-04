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
