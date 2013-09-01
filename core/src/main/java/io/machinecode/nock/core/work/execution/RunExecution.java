package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.Worker;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunExecution extends ExecutableImpl {
    final Worker worker;
    final ExecutionWork work;
    final Context context;

    public RunExecution(final Worker worker, final ExecutionWork work, final Context context) {
        this.worker = worker;
        this.work = work;
        this.context = context;
    }

    @Override
    public void run(final Transport transport) throws Exception {
        work.before(worker, transport, context);
        work.run(worker, transport, context);
        //final Future<StepExecution[]> execution = transport.run(work, context);
        //context.setCurrentExecutionResults(execution);
    }
}
