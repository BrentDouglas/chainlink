package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.work.Listener;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class Notify implements Listener {
    final Object that;

    public Notify(final Object that) {
        this.that = that;
    }

    @Override
    public void run() {
        synchronized (that) {
            that.notifyAll();
        }
    }
}
