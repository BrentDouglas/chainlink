package io.machinecode.chainlink.ee.tomee;

import org.apache.openejb.batchee.BatchObserverProducer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class TomeeObserverProducer implements BatchObserverProducer {
    @Override
    public Object produce() {
        return new TomeeObserver();
    }
}
