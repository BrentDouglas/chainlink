package io.machinecode.chainlink.spi.execution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Worker {

    WorkerId getId();

    void execute(final ExecutableEvent event);

    void callback(final CallbackEvent event);
}
