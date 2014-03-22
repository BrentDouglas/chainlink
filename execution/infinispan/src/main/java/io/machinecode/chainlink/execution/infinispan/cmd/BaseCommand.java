package io.machinecode.chainlink.execution.infinispan.cmd;

import io.machinecode.chainlink.execution.infinispan.InfinispanExecutor;
import org.infinispan.AdvancedCache;
import org.infinispan.commands.remote.CacheRpcCommand;
import org.infinispan.query.impl.CommandInitializer;
import org.infinispan.query.impl.CustomQueryCommand;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseCommand implements CacheRpcCommand, CustomQueryCommand {

    String cacheName;
    Address origin;
    Address remote;
    UUID uuid;

    AdvancedCache<Address, InfinispanExecutor> cache;

    protected BaseCommand(final String cacheName, final Address remote, final UUID uuid) {
        this.cacheName = cacheName;
        this.remote = remote;
        this.uuid = uuid;
    }

    @Override
    public void fetchExecutionContext(final CommandInitializer commandInitializer) {
        this.cache = commandInitializer.getCacheManager()
                .<Address, InfinispanExecutor>getCache(cacheName)
                .getAdvancedCache();
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ cacheName, remote, uuid };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.cacheName = (String)parameters[0];
        this.remote = (Address)parameters[1];
        this.uuid = (UUID)parameters[2];
    }

    @Override
    public Address getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(final Address origin) {
        this.origin = origin;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }

    @Override
    public boolean canBlock() {
        return false;
    }
}
