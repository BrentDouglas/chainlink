package io.machinecode.chainlink.spi.execution;

/**
 * <p>A representation of a {@link Thread}.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Worker {

    /**
     * @return A unique identifier.
     */
    WorkerId getId();

    void execute(final ExecutableEvent event);

    void callback(final CallbackEvent event);
}
