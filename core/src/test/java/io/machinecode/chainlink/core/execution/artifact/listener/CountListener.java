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
package io.machinecode.chainlink.core.execution.artifact.listener;

import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CountListener implements JobListener, StepListener, ChunkListener, ItemReadListener, ItemWriteListener,
                                           ItemProcessListener , RetryReadListener, RetryWriteListener, RetryProcessListener,
                                           SkipReadListener, SkipWriteListener, SkipProcessListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void beforeChunk() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onError(final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void afterChunk() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void beforeStep() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void afterStep() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void beforeJob() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void afterJob() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void beforeProcess(final Object item) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void afterProcess(final Object item, final Object result) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onProcessError(final Object item, final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void beforeRead() throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void afterRead(final Object item) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onReadError(final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void beforeWrite(final List<Object> items) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void afterWrite(final List<Object> items) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onWriteError(final List<Object> items, final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onRetryProcessException(final Object item, final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onRetryReadException(final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onRetryWriteException(final List<Object> items, final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onSkipProcessItem(final Object item, final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onSkipReadItem(final Exception exception) throws Exception {
        count.incrementAndGet();
    }

    @Override
    public void onSkipWriteItem(final List<Object> items, final Exception exception) throws Exception {
        count.incrementAndGet();
    }
}
