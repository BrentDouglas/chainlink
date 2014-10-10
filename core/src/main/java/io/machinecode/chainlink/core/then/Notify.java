package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.OnComplete;
import org.jboss.logging.Logger;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class Notify implements OnComplete { //TODO Why is this serializable?

    private static final Logger log = Logger.getLogger(Notify.class);

    final Object lock;

    public Notify(final Object lock) {
        this.lock = lock;
    }

    @Override
    public void complete(final int state) {
        synchronized (lock) {
            lock.notifyAll();
        }
        log.tracef(Messages.get("CHAINLINK-027000.notify.listener"), lock);
    }
}
