package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.core.registry.LocalJobRegistry;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.transport.infinispan.callable.FindJobRegistryWithChainIdCallable;
import io.machinecode.chainlink.spi.then.Chain;
import org.infinispan.remoting.transport.Address;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanJobRegistry extends LocalJobRegistry {

    final InfinispanRegistry registry;
    final long jobExecutionId;

    public InfinispanJobRegistry(final InfinispanRegistry registry, final long jobExecutionId) {
        this.registry = registry;
        this.jobExecutionId = jobExecutionId;
    }

    public Chain<?> getLocalChain(final ChainId id) {
        return super.getChain(id);
    }

    @Override
    public Chain<?> getChain(final ChainId id) {
        final Chain<?> chain = super.getChain(id);
        if (chain != null) {
             return chain;
        }
        final List<Address> members = registry.rpc.getMembers();
        final List<Future<Address>> futures = new ArrayList<Future<Address>>();
        for (final Address address : members) {
            if (address.equals(registry.local)) {
                continue;
            }
            futures.add(registry.distributor.submit(address, new FindJobRegistryWithChainIdCallable(jobExecutionId, id)));
        }
        for (final Future<Address> future : futures) {
            try {
                //TODO Search these for completes rather that .get() them in order
                final Address address = future.get();
                if (address == null) {
                    continue;
                }
                if (address.equals(registry.local)) {
                    throw new IllegalStateException(); //Also should not have been distributed
                } else {
                    //TODO Cache these?
                    return new LocalChain(registry, address, jobExecutionId, id);
                }
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            } catch (final ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.registry.no.job", jobExecutionId));
    }


}
