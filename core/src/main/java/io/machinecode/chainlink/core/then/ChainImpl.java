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
package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;
import org.jboss.logging.Logger;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ChainImpl<T> extends BaseChain<T> {

    private static final Logger log = Logger.getLogger(ChainImpl.class);

    protected volatile Chain<?> link;

    @Override
    public void link(final Chain<?> that) {
        if (that == null) {
            throw new IllegalArgumentException(Messages.format("CHAINLINK-004200.chain.argument.required", "link"));
        }
        final Iterable<OnLink> onLinks;
        synchronized (_linkLock) {
            this.link = that;
            that.previous(this);
            synchronized (lock) {
                onLinks = getEvents(ON_LINK);
            }
        }
        RuntimeException exception = null;

        for (final OnLink on : onLinks) {
            try {
                on.link(that);
            } catch (final Throwable e) {
                if (exception == null) {
                    exception = new RuntimeException(Messages.format("CHAINLINK-004002.chain.get.exception"), e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        this.notifyLinked();
        if (exception != null) {
            log().warnf(exception, Messages.format("CHAINLINK-004002.chain.get.exception"));
            throw exception;
        }
    }

    @Override
    public ChainImpl<T> onLink(final OnLink then) {
        if (then == null) {
            throw new IllegalArgumentException(Messages.format("CHAINLINK-004200.chain.argument.required", "onLink"));
        }
        synchronized (_linkLock) {
            if (this.link != null) {
                try {
                    then.link(this.link);
                } catch (final Throwable e) {
                    final RuntimeException exception = new RuntimeException(Messages.format("CHAINLINK-004004.chain.link.exception"), e);
                    log().warnf(exception, Messages.format("CHAINLINK-004004.chain.link.exception"));
                    throw exception;
                }
            } else {
                synchronized (lock) {
                    addEvent(ON_LINK, then);
                }
            }
        }
        return this;
    }

    @Override
    public void awaitLink() throws InterruptedException, ExecutionException, CancellationException {
        synchronized (_linkLock) {
            while (this.link == null) {
                _linkLock.wait();
            }
        }
        try {
            this.link.get();
        } catch (final ExecutionException | CancellationException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public void awaitLink(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException, CancellationException {
        final long end = System.currentTimeMillis() + unit.toMillis(timeout);
        synchronized (_linkLock) {
            while (this.link == null) {
                _linkLock.wait(_tryTimeout(end));
            }
        }
        try {
            this.link.get(_tryTimeout(end), MILLISECONDS);
        } catch (final ExecutionException | CancellationException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
