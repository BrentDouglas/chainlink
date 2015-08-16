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
package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.execution.Executor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventedExecutorFactory implements ExecutorFactory {

    final ThreadFactoryLookup threadFactory;

    public EventedExecutorFactory() {
        this(new DefaultThreadFactory());
    }

    public EventedExecutorFactory(final ThreadFactoryLookup threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public Executor produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new EventedExecutor(dependencies, properties, this.threadFactory.lookupThreadFactory(properties));
    }
}
