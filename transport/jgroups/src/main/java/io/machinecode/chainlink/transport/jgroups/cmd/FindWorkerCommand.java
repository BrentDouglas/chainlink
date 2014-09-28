package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FindWorkerCommand implements Command<Address> {

    final WorkerId workerId;

    public FindWorkerCommand(final WorkerId workerId) {
        this.workerId = workerId;
    }

    @Override
    public Address invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        return registry.hasWorker(workerId) ? registry.getLocal() : null;
    }
}
