package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.work.Bucket;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.TaskWork;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Transport {

    TransactionManager getTransactionManager();

    Repository getRepository() throws Exception;

    Deferred execute(Plan plan);

    Synchronization wrapSynchronization(Synchronization synchronization);

    InjectionContext createInjectionContext(Context context);

    Bucket getBucket(TaskWork work);

    void setBucket(Bucket bucket, TaskWork work);

    Bucket evictBucket(TaskWork work);
}
