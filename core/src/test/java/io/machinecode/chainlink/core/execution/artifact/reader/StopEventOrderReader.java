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
package io.machinecode.chainlink.core.execution.artifact.reader;

import io.machinecode.chainlink.core.base.Reference;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StopEventOrderReader implements ItemReader {
    public static final Reference<Boolean> hasStopped = new Reference<>(false);

    private boolean read = false;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CLOSE);
    }

    public static void await() throws InterruptedException {
        synchronized (hasStopped) {
            while (hasStopped.get()) {
                hasStopped.wait(1000);
            }
        }
    }

    @Override
    public Object readItem() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READ);
        synchronized (hasStopped) {
            while (!hasStopped.get()) {
                hasStopped.notifyAll();
                hasStopped.wait(1000);
            }
        }
        if (!read) {
            read = true;
            return new Object();
        } else {
            return null;
        }
    }

    public static void go() throws Exception {
        hasStopped.set(true);
        synchronized (hasStopped) {
            hasStopped.notifyAll();
        }
    }

    public static void reset() throws Exception {
        hasStopped.set(false);
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CHECKPOINT);
        return null;
    }
}
