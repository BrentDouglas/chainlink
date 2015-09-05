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
package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestEnvironment  implements Environment, AutoCloseable {
    final JobOperatorImpl operator;

    public TestEnvironment(final JobOperatorImpl operator) {
        this.operator = operator;
    }

    @Override
    public JobOperatorImpl getJobOperator(final String name) throws NoConfigurationWithIdException {
        if (Constants.DEFAULT.equals(name)) {
            return operator;
        }
        throw new NoConfigurationWithIdException("No operator for name " + name);
    }

    @Override
    public SubSystemSchema<?,?,?> getConfiguration() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SubSystemSchema<?,?,?> setConfiguration(final Configure configure) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void reload() throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void close() throws Exception {
        operator.close();
    }
}
