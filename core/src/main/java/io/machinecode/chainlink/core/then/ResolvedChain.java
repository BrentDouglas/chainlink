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

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.then.OnLink;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ResolvedChain<T> extends BaseChain<T> {

    public ResolvedChain(final T value) {
        resolve(value);
        notifyLinked();
    }

    @Override
    public void link(final Chain<?> that) {
        throw new IllegalStateException(); //TODO Message This is a terminal link
    }

    @Override
    public ResolvedChain<T> onLink(final OnLink then) {
        // noop
        return this;
    }

    @Override
    public void awaitLink() {
        // no op
    }

    @Override
    public void awaitLink(final long timeout, final TimeUnit unit) throws InterruptedException {
        // no op
    }
}
