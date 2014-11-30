package io.machinecode.chainlink.test.core.execution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Reference<T> {

    private volatile T value;

    public Reference(final T value) {
        this.value = value;
    }

    public Reference() {
        this(null);
    }

    /**
     * @return The value
     */
    public T get() {
        return value;
    }

    /**
     * @param value The new value
     * @return The old value
     */
    public T set(final T value) {
        final T that = this.value;
        this.value = value;
        return that;
    }
}
