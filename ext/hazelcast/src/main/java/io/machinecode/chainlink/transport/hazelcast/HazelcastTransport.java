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
package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.FutureDeferred;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.SomeDeferred;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastTransport extends DistributedTransport<String> {

    private static final Logger log = Logger.getLogger(HazelcastTransport.class);

    protected final HazelcastInstance hazelcast;
    protected final IExecutorService executor;
    protected final Executor network;

    final Member local;
    protected volatile List<Member> remotes;
    private final Map<String,Member> members = new THashMap<>();
    private final Object memberLock = new Object();
    private final RemoteMemberSelector selector;

    public HazelcastTransport(final Dependencies dependencies, final PropertyLookup properties, final HazelcastInstance hazelcast, final IExecutorService executor) throws Exception {
        super(dependencies, properties);
        this.hazelcast = hazelcast;
        this.executor = executor;
        this.network= Executors.newCachedThreadPool();

        final Cluster cluster = hazelcast.getCluster();
        this.local = cluster.getLocalMember();
        this.remotes = _calculateRemoteMembers(cluster.getMembers());
        this.selector = new RemoteMemberSelector(this.local);
        cluster.addMembershipListener(new MembershipListener() {
            @Override
            public void memberAdded(final MembershipEvent event) {
                HazelcastTransport.this.remotes = _calculateRemoteMembers(event.getMembers());
            }

            @Override
            public void memberRemoved(final MembershipEvent event) {
                HazelcastTransport.this.remotes = _calculateRemoteMembers(event.getMembers());
            }

            @Override
            public void memberAttributeChanged(final MemberAttributeEvent memberAttributeEvent) {
            }
        });
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        super.open(configuration);
        //TODO This needs to have some ID in it
        final String key = Configuration.class.getCanonicalName();
        final ConcurrentMap<String, Object> context = this.hazelcast.getUserContext();
        if (context.containsKey(key)) {
            throw new IllegalStateException("A transport is already configured for this instance."); //TODO Message
        }
        context.put(key, configuration);
        log.infof("HazelcastTransport %s has started.", this.local); //TODO Message
    }

    @Override
    public void close() throws Exception {
        log.infof("HazelcastTransport %s is shutting down.", this.local); //TODO Message
        super.close();
        this.hazelcast.getUserContext().remove(Configuration.class.getCanonicalName());
    }

    @Override
    public String getAddress() {
        return local.getUuid();
    }

    @Override
    public <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit) {
        if (!(address instanceof String)) {
            return new RejectedDeferred<T, Throwable, Object>(new Exception("Expected " + String.class.getName() + ". Found " + address.getClass())); //TODO Message
        }

        final Member to;
        synchronized (memberLock) {
            to = members.get(address);
        }
        if (to == null) {
            return new RejectedDeferred<T, Throwable, Object>(new Exception("No member with UUID " + address)); //TODO Message
        }
        try {
            log.tracef("Invoking %s on %s.", command, address); //TODO Message
            final FutureDeferred<T, Object> run = new FutureDeferred<>(this.executor.<T>submitToMember(
                    new Invocation<>(command, this.local),
                    to
            ), timeout, unit);
            network.execute(run);
            return run;
        } catch (final Exception e) {
            return new RejectedDeferred<T, Throwable, Object>(e);
        }
    }

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
        log.tracef("Invoking %s on all remotes.", command); //TODO Message
        try {
            final Map<Member,Future<T>> map = this.executor.submitToMembers(
                    new Invocation<>(command, this.local),
                    this.selector
            );
            final List<Promise<T,Throwable,?>> promises = new ArrayList<>();
            for (final Map.Entry<Member, Future<T>> entry : map.entrySet()) {
                if (entry.getKey().equals(local)) {
                    continue;
                }
                final FutureDeferred<T,?> run = new FutureDeferred<>(entry.getValue(), timeout, unit);
                network.execute(run);
                promises.add(run);
            }
            return new SomeDeferred<>(promises);
        } catch (final Throwable e) {
            return new RejectedDeferred<>(e);
        }
    }

    private List<Member> _calculateRemoteMembers(final Collection<Member> members) {
        synchronized (memberLock) {
            this.members.clear();
            for (final Member member : members) {
                this.members.put(member.getUuid(), member);
            }
        }
        final List<Member> that = new ArrayList<>(members);
        that.remove(this.local);
        return that;
    }

    public static class Invocation<T> implements Callable<T>, Serializable, HazelcastInstanceAware {
        private static final long serialVersionUID = 1L;

        private final Command<T> command;
        private final String origin;
        private transient Configuration configuration;

        public Invocation(final Command<T> command, final Member origin) {
            this.command = command;
            this.origin = origin.getUuid();
        }

        @Override
        public void setHazelcastInstance(final HazelcastInstance hazelcast) {
            this.configuration = (Configuration) hazelcast.getUserContext().get(Configuration.class.getCanonicalName());
        }

        @Override
        public T call() throws Exception {
            if (this.configuration == null) {
                throw new IllegalStateException(); //TODO Message
            }
            try {
                return command.perform(this.configuration, this.origin);
            } catch (final Exception e) {
                throw e;
            } catch (final Throwable e) {
                throw new Exception(e);
            }
        }
    }

    private static class RemoteMemberSelector implements MemberSelector {

        final Member local;

        private RemoteMemberSelector(final Member local) {
            this.local = local;
        }

        @Override
        public boolean select(final Member member) {
            return !member.equals(local);
        }
    }
}
