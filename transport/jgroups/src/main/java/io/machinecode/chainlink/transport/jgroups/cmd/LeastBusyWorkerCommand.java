package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import io.machinecode.chainlink.transport.jgroups.JGroupsThreadId;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LeastBusyWorkerCommand implements Command<JGroupsThreadId> {

    public LeastBusyWorkerCommand() {
        //
    }

    @Override
    public JGroupsThreadId invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        return registry.leastBusyWorker();
    }
}
