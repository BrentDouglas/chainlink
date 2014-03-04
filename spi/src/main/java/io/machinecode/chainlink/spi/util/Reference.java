package io.machinecode.chainlink.spi.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Reference<T> {
    private T value;

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
