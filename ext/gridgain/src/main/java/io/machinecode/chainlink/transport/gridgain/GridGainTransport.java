/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.RejectedDeferred;
import org.gridgain.grid.Grid;
import org.jboss.logging.Logger;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainTransport extends DistributedTransport<UUID> {

    private static final Logger log = Logger.getLogger(GridGainTransport.class);

    final Grid grid;
    final UUID local;

    public GridGainTransport(final Dependencies dependencies, final PropertyLookup properties, final Grid grid) throws Exception {
        super(dependencies, properties);
        this.grid = grid;
        this.local = grid.localNode().id();
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        super.open(configuration);
        if (grid.nodeLocalMap().putIfAbsent(Configuration.class.getName(), configuration) != null) {
            throw new IllegalStateException("A transport is already configured for this grid"); //TODO Message
        }
        log.infof("GridGainTransport %s started.", this.local); //TODO Message
    }

    @Override
    public void close() throws Exception {
        log.infof("GridGainTransport %s is shutting down.", this.local); //TODO Message
        super.close();
        grid.nodeLocalMap().remove(Configuration.class.getName());
    }

    @Override
    public UUID getAddress() {
        return local;
    }

    @Override
    public <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command,
                                 final long timeout, final TimeUnit unit) {
        if (!(address instanceof UUID)) {
            return new RejectedDeferred<T, Throwable, Object>(new Exception("Expected " + UUID.class.getName() + ". Found " + address.getClass())); //TODO Message
        }
        final UUID uuid = (UUID) address;
        final GridGainDeferred<T> ret = new GridGainDeferred<>(timeout, unit);
        try {
            log.tracef("Invoking %s on %s.", command, address);
            this.grid.forNodeId(uuid)
                    .compute()
                    .call(new GridGainCallable<>(command, local))
                    .listenAsync(ret);
        } catch (final Throwable e) {
            ret.reject(e);
        }
        return ret;
    }

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
        log.tracef("Invoking %s on all remotes.", command);
        final GridGainDeferred<Collection<T>> ret = new GridGainDeferred<>(timeout, unit);
        try {
            this.grid.forRemotes()
                    .compute()
                    .broadcast(new GridGainCallable<>(command, local))
                    .listenAsync(ret);
        } catch (final Throwable e) {
            ret.reject(e);
        }
        return ret;
    }
}
