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
package io.machinecode.chainlink.core.execution.artifact.listener;

import io.machinecode.chainlink.core.execution.artifact.exception.FailListenException;

import javax.batch.api.chunk.listener.ItemWriteListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailBeforeWriteListener implements ItemWriteListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void beforeWrite(final List<Object> items) throws Exception {
        count.incrementAndGet();
        throw new FailListenException();
    }

    @Override
    public void afterWrite(final List<Object> items) throws Exception {
        //
    }

    @Override
    public void onWriteError(final List<Object> items, final Exception exception) throws Exception {
        //
    }
}
