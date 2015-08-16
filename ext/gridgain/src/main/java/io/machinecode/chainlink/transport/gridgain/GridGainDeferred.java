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
package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.then.core.DeferredImpl;
import org.gridgain.grid.GridFuture;
import org.gridgain.grid.GridFutureCancelledException;
import org.gridgain.grid.util.typedef.CI1;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainDeferred<T> extends DeferredImpl<T,Throwable,Object> implements CI1<GridFuture<T>> {
    private static final long serialVersionUID = 1L;

    final long timeout;
    final TimeUnit unit;

    public GridGainDeferred(final long timeout, final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void apply(final GridFuture<T> future) {
        final T that;
        try {
            that = future.get(timeout, unit);
        } catch (final GridFutureCancelledException e) {
            this.cancel(true);
            return;
        } catch (final Throwable e) {
            this.reject(e);
            return;
        }
        this.resolve(that);
    }
}
