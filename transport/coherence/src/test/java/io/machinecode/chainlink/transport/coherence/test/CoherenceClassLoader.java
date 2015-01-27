package io.machinecode.chainlink.transport.coherence.test;

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
