package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecuteCommand implements Command<Void> {

    final WorkerId workerId;
    final ExecutableEvent event;

    public ExecuteCommand(final WorkerId workerId, final ExecutableEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Void invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        registry.getWorker(workerId).execute(event);
        return null;
    }
}
