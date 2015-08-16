/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class EventedWorker implements Worker, Runnable, AutoCloseable, Comparable<EventedWorker> {

    private static final Logger log = Logger.getLogger(EventedWorker.class);

    protected final WorkerId workerId;
    protected final Object lock = new Object();
    protected final Queue<ExecutableEvent> executables = new LinkedList<>();
    protected final Queue<CallbackEvent> callbacks = new LinkedList<>();
    protected final Configuration configuration;
    protected volatile boolean inCall = false;
    protected volatile boolean started = true;
    protected volatile boolean stopped = false;
    protected volatile boolean finished = false;

    public EventedWorker(final Configuration configuration) {
        this.configuration = configuration;
        this.workerId = new UUIDId(configuration.getTransport());
    }

    @Override
    public WorkerId getId() {
        return workerId;
    }

    public boolean isActive() {
        return started && !finished;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        final Executable executable = event.getExecutable();
        synchronized (lock) {
            executables.add(event);
            log.debugf(Messages.get("CHAINLINK-024005.worker.add.executable"), executable.getContext(), this, event);
            lock.notifyAll();
        }
    }

    @Override
    public void callback(final CallbackEvent event) {
        synchronized (lock) {
            callbacks.add(event);
            log.debugf(Messages.get("CHAINLINK-024006.worker.add.callback"), event.getContext(), this, event);
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        this.started = true;
        log.infof(Messages.get("CHAINLINK-024012.worker.started"), this);
        boolean empty;
        do {
            _awaitIfEmpty();
            empty = _runExecutable();
        } while (!stopped || !empty);
        this.finished = true;
        log.infof(Messages.get("CHAINLINK-024011.worker.stopped"), this);
    }

    private boolean _runExecutable() {
        final CallbackEvent cb;
        final ExecutableEvent ex;
        synchronized (lock) {
            cb = callbacks.poll();
            if (cb == null) {
                ex = executables.poll();
            } else {
                ex = null;
            }
        }
        this.inCall = true; //TODO Is this the right spot to switch
        final ExecutionContext previous;
        final Executable executable;
        final ChainId chainId;
        if (cb == null) {
            if (ex == null) {
                this.inCall = false;
                return true;
            }
            executable = ex.getExecutable();
            previous = null;
            chainId = ex.getChainId();
        } else {
            try {
                executable = configuration.getRegistry()
                        .getExecutable(cb.getJobExecutionId(), cb.getExecutableId());
                LocalRegistry.assertExecutable(executable, cb.getJobExecutionId(), cb.getExecutableId());
                previous = cb.getContext();
                chainId = cb.getChainId();
            } catch (final Throwable e) {
                log.errorf(e, Messages.get("CHAINLINK-024007.worker.fetch.exception"), this, cb);
                this.inCall = false;
                return false;
            }
        }
        final ExecutionContext context = executable.getContext();
        try {
            final Chain<?> chain = configuration.getRegistry()
                    .getChain(context.getJobExecutionId(), chainId);
            LocalRegistry.assertChain(chain, context.getJobExecutionId(), chainId);
            log.tracef(Messages.get("CHAINLINK-024008.worker.start.execution"), context, this, executable);
            executable.execute(configuration, chain, workerId, previous);
            log.tracef(Messages.get("CHAINLINK-024009.worker.finish.execution"), context, this, executable);
        } catch (final Throwable e) {
            log.errorf(e, Messages.get("CHAINLINK-024004.worker.failed.execution"), context, this, executable);
        }
        this.inCall = false;
        return false;
    }

    private void _awaitIfEmpty() {
        try {
            synchronized (lock) {
                if (executables.isEmpty() && callbacks.isEmpty() && !stopped) {
                    log.tracef(Messages.get("CHAINLINK-024002.worker.waiting"), this);
                    lock.wait();
                    log.tracef(Messages.get("CHAINLINK-024003.worker.awake"), this);
                }
            }
        } catch (final InterruptedException ie) {
            log.infof(Messages.get("CHAINLINK-024001.worker.interrupted"), this);
        }
    }

    @Override
    public void close() throws InterruptedException {
        stopped = true;
        log.infof(Messages.get("CHAINLINK-024010.worker.stopping"), this);
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{workerId=" + workerId + ",executables=" + executables.size() + ",callbacks=" + callbacks.size() + ",running=" + isActive() + "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EventedWorker that = (EventedWorker) o;
        return workerId.equals(that.workerId);
    }

    @Override
    public int hashCode() {
        return workerId.hashCode();
    }

    @Override
    public int compareTo(final EventedWorker that) {
        if (this.equals(that)) {
            return 0;
        }
        int ret = Integer.compare(this.executables.size(), that.executables.size());
        if (ret != 0) {
            return ret;
        }
        ret =  Integer.compare(this.callbacks.size(), that.callbacks.size());
        if (ret != 0) {
            return ret;
        }
        if (this.inCall) {
            if (that.inCall) {
                return 0;
            }
            return 1;
        } else {
            return -1;
        }
    }
}
