package io.machinecode.chainlink.core.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Configure {

    /**
     * @param subsystem The schema to mutate. Depending on the environment, some mutators
     *                  may not work.
     * @throws Exception On an implementation specific error.
     */
    void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception;
}
