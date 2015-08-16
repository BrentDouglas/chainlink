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
package io.machinecode.chainlink.coherence.test.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceClassLoader extends ClassLoader {

    private final ClassLoader delegate;

    public CoherenceClassLoader(final ClassLoader delegate) {
        super(null);
        this.delegate = delegate;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (name.startsWith("com.tangosol") || (name.startsWith("io.machinecode.chainlink") && name.contains("coherence"))) {
            return loadClass(name, false);
        }
        return delegate.loadClass(name);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        final InputStream stream = delegate.getResourceAsStream(name.replace(".", "/") + ".class");
        if (stream == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            try {
                final byte[] buf = new byte[1024];
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                int i;
                while ((i = stream.read(buf)) != -1) {
                    out.write(buf, 0, i);
                }
                final byte[] bytes = out.toByteArray();
                return defineClass(name, bytes, 0, bytes.length);
            } finally {
                stream.close();
            }
        } catch (final IOException e) {
            throw new ClassNotFoundException(name);
        }
    }

    @Override
    public URL getResource(final String name) {
        return delegate.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        return delegate.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(final String name) {
        return delegate.getResourceAsStream(name);
    }

    @Override
    public void setDefaultAssertionStatus(final boolean enabled) {
        delegate.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(final String packageName, final boolean enabled) {
        delegate.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(final String className, final boolean enabled) {
        delegate.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        delegate.clearAssertionStatus();
    }
}
