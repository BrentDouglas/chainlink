package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.work.Bucket;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.TaskWork;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Transport {

    TransactionManager getTransactionManager();

    ExecutionRepository getRepository();

    Context getContext(long jobExecutionId);

    Deferred<?> getJob(long jobExecutionId);

    Deferred<?> execute(long jobExecutionId, Executable executable, Plan plan);

    Deferred<?> executeJob(long jobExecutionId, JobWork job, Context context);

    void finalizeJob(long jobExecutionId);

    InjectionContext createInjectionContext(Context context);

    Bucket getBucket(TaskWork work);

    void setBucket(Bucket bucket, TaskWork work);

    Bucket evictBucket(TaskWork work);
}
