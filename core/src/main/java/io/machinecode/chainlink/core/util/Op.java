package io.machinecode.chainlink.core.util;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public enum Op {
    ADD,
    UPDATE,
    REMOVE;

    public boolean isActive(final Op... ops) {
        for (final Op op : ops) {
            if (this == op) {
                return true;
            }
        }
        return false;
    }
}
