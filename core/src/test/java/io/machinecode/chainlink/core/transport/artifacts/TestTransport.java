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
package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.CallableDeferred;
import io.machinecode.then.core.DeferredImpl;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.ResolvedDeferred;
import io.machinecode.then.core.SomeDeferred;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestTransport extends DistributedTransport<String> {

    final ConcurrentMap<String, TestTransport> transports;
    final String local;
    final List<String> remotes;
    protected final ExecutorService executor;

    public TestTransport(final ConcurrentMap<String, TestTransport> transports, final String local, final List<String> remotes, final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        super(dependencies, properties);
        this.executor = Executors.newCachedThreadPool();
        this.transports = transports;
        this.local = local;
        this.remotes = remotes;
    }

    @Override
    public String getAddress() {
        return local;
    }

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
        final List<String> remotes = this.remotes;
        final List<Promise<T,Throwable,?>> promises = new ArrayList<>(remotes.size());
        for (final String remote : remotes) {
            promises.add(invokeRemote(remote, command));
        }
        return new SomeDeferred<>(promises);
    }

    @Override
    public <T> Promise<T, Throwable, Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit) {
        if (!(address instanceof String)) {
            return new RejectedDeferred<T, Throwable,Object>(new IllegalStateException("Should get a string"));
        }
        final DeferredImpl<T,Throwable,Object> def = new DeferredImpl<>();
        try {
            transports.get(address)
                    .invokeLocal(def, command, getAddress())
                    .get(timeout, unit);
        } catch (final Throwable e) {
            def.reject(e);
        }
        return def;
    }

    public <T> Future<?> invokeLocal(final DeferredImpl<T,Throwable,Object> def, final Command<T> command, final String origin) throws Exception {
        final TestTransport self = this;
        return executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    def.resolve(command.perform(self.configuration, origin));
                } catch (final Throwable e) {
                    def.reject(e);
                }
            }
        });
    }
}
