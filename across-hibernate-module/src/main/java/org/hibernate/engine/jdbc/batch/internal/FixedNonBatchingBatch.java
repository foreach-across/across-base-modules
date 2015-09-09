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

import org.hibernate.JDBCException;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by marc on 4/09/2014.
 */
public class FixedNonBatchingBatch extends NonBatchingBatch {

    private JdbcCoordinator jdbcCoordinator;

    protected FixedNonBatchingBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        super(key, jdbcCoordinator);
        this.jdbcCoordinator = jdbcCoordinator;
    }

    @Override
    public void addToBatch() {
        notifyObserversImplicitExecution();
        for ( Map.Entry<String,PreparedStatement> entry : getStatements().entrySet() ) {
            try {
                final PreparedStatement statement = entry.getValue();
                final int rowCount = jdbcCoordinator.getResultSetReturn().executeUpdate( statement );
                getKey().getExpectation().verifyOutcome( rowCount, statement, 0 );
                jdbcCoordinator.release( statement );
            }
            catch ( SQLException e ) {
                abortBatch();
                throw sqlExceptionHelper().convert( e, "could not execute non-batched batch statement", entry.getKey() );
            }
            catch (JDBCException e) {
                abortBatch();
                throw e;
            }
        }

        getStatements().clear();
    }
}
