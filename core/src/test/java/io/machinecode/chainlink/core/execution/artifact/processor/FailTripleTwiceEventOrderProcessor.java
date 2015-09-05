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
package io.machinecode.chainlink.core.execution.artifact.processor;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailProcessException;

import javax.batch.api.chunk.ItemProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailTripleTwiceEventOrderProcessor implements ItemProcessor {

    int count = 0;

    @Override
    public Object processItem(final Object item) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.PROCESS);
        try {
            switch (count) {
                case 1:
                    throw new Exception();
                case 2:
                case 3:
                    throw new FailProcessException();
                case 7:
                    throw new Exception();
                case 8:
                case 9:
                    throw new FailProcessException();
                default:
                    return item;
            }
        } finally {
            ++count;
        }
    }
}
