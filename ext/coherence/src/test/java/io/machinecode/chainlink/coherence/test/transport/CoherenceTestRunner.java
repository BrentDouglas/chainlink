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
package io.machinecode.chainlink.coherence.test.transport;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceTestRunner extends BlockJUnit4ClassRunner {

    public static ClassLoader loader;

    public CoherenceTestRunner(final Class<?> clazz) throws InitializationError {
        super(reload(clazz));
        loader = getTestClass().getJavaClass().getClassLoader();
    }

    private static Class<?> reload(final Class<?> clazz) throws InitializationError {
        final CoherenceClassLoader loader = new CoherenceClassLoader(clazz.getClassLoader());
        try {
            return loader.loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            super.run(notifier);
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }
}
