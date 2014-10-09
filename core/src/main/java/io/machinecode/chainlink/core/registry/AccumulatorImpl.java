package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.registry.Accumulator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class AccumulatorImpl implements Accumulator {

    private long count = 0;

    @Override
    public long incrementAndGetCallbackCount() {
        return ++count;
    }
}
