package io.machinecode.chainlink.core.deferred;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class Notify implements Listener {

    private static final Logger log = Logger.getLogger(Notify.class);

    final Object lock;

    public Notify(final Object lock) {
        this.lock = lock;
    }

    @Override
    public void run(final Deferred<?> deferred) {
        synchronized (lock) {
            lock.notifyAll();
        }
        log.tracef(Messages.get("CHAINLINK-027000.notify.listener"), lock);
    }
}
