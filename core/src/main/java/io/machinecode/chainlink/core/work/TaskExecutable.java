package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.context.MutableStepContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.TaskWork;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnCancel;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class TaskExecutable extends ExecutableImpl<TaskWork> implements Serializable {

    private static final Logger log = Logger.getLogger(TaskExecutable.class);

    final int timeout;

    public TaskExecutable(final ExecutableId parentId, final TaskWork work, final ExecutionContext context, final ExecutionRepositoryId executionRepositoryId, final int timeout) {
        super(parentId, context, work, executionRepositoryId, null);
        this.timeout = timeout;
    }

    @Override
    public void doExecute(final RuntimeConfiguration configuration, final Chain<?> chain, final WorkerId workerId,
                          final ExecutableId parentId, final ExecutionContext childContext) throws Throwable {
        //TODO Check ordering of this and check it is allowed to run in this thread
        chain.onCancel(new OnCancel() {
            @Override
            public boolean cancel(final boolean interrupt) {
                log.debugf(Messages.format("CHAINLINK-023005.work.task.cancel", context));
                work.cancel(configuration, context);
                return true;
            }
        });
        final MutableStepContext stepContext = context.getStepContext();
        final MutableJobContext jobContext = context.getJobContext();
        final Executable callback = configuration.getRegistry()
                .getJobRegistry(context.getJobExecutionId())
                .getExecutable(parentId);
        try {
            work.run(configuration, chain, this.executionRepositoryId, this.context, timeout);
            final Chain<?> next = configuration.getExecutor().callback(callback, this.context);
            chain.link(next);
            chain.resolve(null);
        } catch (final Throwable e) {
            log.errorf(e, Messages.format("CHAINLINK-023004.work.task.run.exception", this.context));
            if (e instanceof Exception) {
                stepContext.setException((Exception) e);
            }
            stepContext.setBatchStatus(BatchStatus.FAILED);
            jobContext.setBatchStatus(BatchStatus.FAILED);
            final Chain<?> next = configuration.getExecutor().callback(callback, this.context);
            chain.link(next);
            chain.reject(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
