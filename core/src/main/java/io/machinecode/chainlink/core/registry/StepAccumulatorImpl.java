/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.registry.StepAccumulator;

import javax.batch.api.partition.PartitionReducer;
import javax.transaction.Transaction;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class StepAccumulatorImpl implements StepAccumulator {

    private long count = 0;
    private PartitionReducer.PartitionStatus partitionStatus = PartitionReducer.PartitionStatus.COMMIT;
    private Transaction transaction;
    private Exception exception;

    @Override
    public long incrementAndGetCallbackCount() {
        return ++count;
    }

    @Override
    public PartitionReducer.PartitionStatus getPartitionStatus() {
        return partitionStatus;
    }

    @Override
    public void setPartitionStatusRollback() {
        this.partitionStatus = PartitionReducer.PartitionStatus.ROLLBACK;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public void addException(final Exception exception) {
        if (this.exception == null) {
            this.exception = exception;
        } else {
            this.exception.addSuppressed(exception);
        }
    }
}
