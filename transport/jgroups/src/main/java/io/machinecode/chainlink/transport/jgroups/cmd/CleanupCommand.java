package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CleanupCommand implements Command<Void> {

    final long jobExecutionId;

    public CleanupCommand(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Void invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        registry.unregisterJob(jobExecutionId).get();
        return null;
    }
}
