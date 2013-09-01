package io.machinecode.nock.core.local;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Failure;
import io.machinecode.nock.spi.transport.Synchronization;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Bucket;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.TaskWork;

import javax.transaction.TransactionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransport implements Transport {

    final ExecutorService executor;
    final Repository repository;

    public LocalTransport(final Repository repository, final ExecutorService executor) {
        this.repository = repository;
        this.executor = executor;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return new LocalTransactionManager();
    }

    @Override
    public Repository getRepository() throws Exception {
        return repository;
    }

    @Override
    public Future<Void> executeOnParentThread(final Executable executable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Future<Void> executeOnThisThread(final Executable executable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Future<Void> executeOnAnyThread(final Executable executable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Future<Void> executeOnAnyThreadThenOnThisThread(final Executable[] executables, final Executable then) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Future<Void> fail(final Failure failure, final Exception exception) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Future<Void> runJob(final JobWork work, final Context context) throws Exception {
        return null; //executor.submit(new ExecutableImpl<Serializable>(context.getJobExecutionId()) {}).get();
    }

    @Override
    public Synchronization wrapSynchronization(final Synchronization synchronization) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InjectionContext createInjectionContext(final Context context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBucket(final Bucket bucket, final TaskWork work) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bucket getBucket(final TaskWork work) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class JobRun implements Runnable {
        final Transport transport;
        final Executable executable;

        public JobRun(final Transport transport, final Executable executable) {
            this.transport = transport;
            this.executable = executable;
        }

        @Override
        public void run() {
            executable.execute(transport);
        }
    }
}
