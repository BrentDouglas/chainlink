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
package io.machinecode.chainlink.core.transaction;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class LocalTransactionManagerFactory implements TransactionManagerFactory {

    final long timeout;
    final TimeUnit unit;

    public LocalTransactionManagerFactory() {
        this(180, TimeUnit.SECONDS);
    }

    public LocalTransactionManagerFactory(final long timeout, final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public TransactionManager produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new LocalTransactionManager(timeout, unit);
    }
}
