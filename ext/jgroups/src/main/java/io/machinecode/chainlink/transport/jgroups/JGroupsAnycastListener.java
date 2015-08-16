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
package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.then.core.DeferredImpl;
import org.jboss.logging.Logger;
import org.jgroups.Address;
import org.jgroups.util.FutureListener;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsAnycastListener<T,P> extends DeferredImpl<List<T>,Throwable,P> implements FutureListener<RspList<T>> {
    private static final Logger log = Logger.getLogger(JGroupsAnycastListener.class);

    final Address local;
    final Command<T> command;

    final long timeout;
    final TimeUnit unit;

    public JGroupsAnycastListener(final Address local, final Command<T> command,final long timeout, final TimeUnit unit) {
        this.local = local;
        this.command = command;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void futureDone(final Future<RspList<T>> future) {
        final RspList<T> that;
        try {
            that = future.get(timeout, unit);
        } catch (final CancellationException e) {
            this.cancel(true);
            return;
        } catch (final Throwable e) {
            this.reject(e);
            return;
        }
        final List<T> ret = new ArrayList<>(that.size());
        for (final Rsp<T> rsp : that) {
            if (!rsp.wasReceived()) {
                if (rsp.wasSuspected()) {
                    log.tracef("Node %s received suspected from %s.", local, rsp.getSender());
                } else if (rsp.wasUnreachable()) {
                    log.tracef("Node %s received unreachable from %s.", local, rsp.getSender());
                }
                continue;
            }
            if (!rsp.hasException()) {
                ret.add(rsp.getValue());
            } else {
                log.tracef(rsp.getException(), "Node %s rReceived exception from %s.", local, rsp.getSender());
            }
        }
        this.resolve(ret);
    }

    @Override
    public void reject(final Throwable fail) {
        log.tracef("Node %s received anycast reject: %s %s.", local, command, fail);
        super.reject(fail);
    }

    @Override
    public void resolve(final List<T> that) {
        log.tracef("Node %s received anycast resolve: %s %s.", local, command, that);
        super.resolve(that);
    }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
        log.tracef("Node %s received anycast cancel: %s.", local, command);
        return super.cancel(mayInterrupt);
    }
}
