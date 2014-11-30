package io.machinecode.chainlink.spi.registry;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public interface Accumulator {

    long incrementAndGetCallbackCount();
}
