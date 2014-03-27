package io.machinecode.chainlink.spi;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Lifecycle {

    void startup();

    void shutdown();
}
