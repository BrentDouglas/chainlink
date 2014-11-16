package io.machinecode.chainlink.ee.tomee;

import org.apache.openejb.assembler.classic.event.AssemblerAfterApplicationCreated;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.observer.event.ObserverAdded;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class TomeeObserver {

    public void initEnvironment(@Observes final ObserverAdded event) {
        if (event.getObserver() == this) {
        }
    }

    public void storeClassLoader(@Observes final AssemblerAfterApplicationCreated init) {
        final Properties properties = new Properties(SystemInstance.get().getProperties());
        properties.putAll(init.getApp().properties);

        final Thread thread = Thread.currentThread();
        final ClassLoader tccl = thread.getContextClassLoader();
        thread.setContextClassLoader(init.getContext().getClassLoader());
        try {

        } finally {
            thread.setContextClassLoader(tccl);
        }
    }
}
