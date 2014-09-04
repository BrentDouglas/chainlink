package io.machinecode.chainlink.spi.registry;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public interface Accumulator {

    long incrementAndGetCallbackCount();
}
