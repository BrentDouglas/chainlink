package io.machinecode.chainlink.spi.management;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ManagementEventListener<T extends ManagementEvent> {

    void listen(final T event);
}
