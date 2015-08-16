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
package io.machinecode.chainlink.core.execution.artifact.writer;

import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteOpenException;
import junit.framework.Assert;

import javax.batch.api.chunk.ItemWriter;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailOpenEventOrderWriter implements ItemWriter {

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_OPEN);
        throw new FailWriteOpenException();
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_CLOSE);
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        Assert.fail();
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        Assert.fail();
        return null;
    }
}
