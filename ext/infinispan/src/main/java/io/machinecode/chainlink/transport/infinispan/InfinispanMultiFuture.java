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
package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.then.api.Deferred;
import org.infinispan.commons.util.concurrent.FutureListener;
import org.infinispan.commons.util.concurrent.NotifyingNotifiableFuture;
import org.infinispan.remoting.responses.ExceptionResponse;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.responses.SuccessfulResponse;
import org.infinispan.remoting.transport.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class InfinispanMultiFuture<T,U> implements NotifyingNotifiableFuture<T> {

    private final Deferred<Iterable<U>,Throwable,?> deferred;
    private final List<Address> addresses;
    private Future<T> io;

    public InfinispanMultiFuture(final Deferred<Iterable<U>,Throwable,?> deferred, final List<Address> addresses) {
        this.deferred = deferred;
        this.addresses = addresses;
    }

    @Override
    public void notifyDone() {
        try {
            final T ret = io.get();
            if (ret == null || !(ret instanceof Map)) {
                deferred.reject(new IllegalStateException()); //TODO Message
                return;
            }
            final List<U> list = new ArrayList<>();
            for (final Address address : addresses) {
                final Response response = ((Map<Address, Response>) ret).get(address);
                if (response == null) {
                    list.add(null);
                } else if (response.isSuccessful()) {
                    list.add((U) ((SuccessfulResponse) response).getResponseValue());
                } else {
                    if (response instanceof ExceptionResponse) {
                        deferred.reject(((ExceptionResponse) response).getException());
                    } else {
                        deferred.reject(new IllegalStateException()); //TODO Message
                    }
                    return;
                }
            }
            deferred.resolve(list);
        } catch (final Throwable e) {
            deferred.reject(e);
        }
    }

    @Override
    public void setNetworkFuture(final Future<T> future) {
        this.io = future;
    }

    @Override
    public InfinispanMultiFuture<T,U> attachListener(final FutureListener<T> listener) {
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return io.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return io.isCancelled();
    }

    @Override
    public boolean isDone() {
        return io.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return io.get();
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return io.get(timeout, unit);
    }
}
