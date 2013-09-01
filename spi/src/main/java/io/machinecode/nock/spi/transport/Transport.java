package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.work.Bucket;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.PartitionWork;
import io.machinecode.nock.spi.work.TaskWork;
import io.machinecode.nock.spi.work.TransitionWork;

import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.api.partition.PartitionCollector;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Transport {

    TransactionManager getTransactionManager();

    Repository getRepository() throws Exception;

    Future<Void> executeOnParentThread(Executable executable);

    Future<Void> executeOnThisThread(Executable executable);

    Future<Void> executeOnAnyThread(Executable executable);

    Future<Void> executeOnAnyThreadThenOnThisThread(Executable[] executables, Executable then);

    Future<Void> fail(Failure failure, Exception exception);

    Future<Void> runJob(JobWork work, Context context) throws Exception;

    Synchronization wrapSynchronization(Synchronization synchronization);

    InjectionContext createInjectionContext(Context context);

    void setBucket(Bucket bucket, TaskWork work);

    Bucket getBucket(TaskWork work);

    //Future<StepExecution[]> run(final ExecutionWork work, final Context context) throws Exception;

    //Future<StepExecution[]> runExecutionsConcurrently(final List<? extends ExecutionWork> work, final Context context) throws Exception;

    //Future<StepExecution> getPartitions(final PartitionWork work, final Context context) throws Exception;

    //Future<?> runTransition(final TransitionWork work, final Context context) throws Exception;
}
