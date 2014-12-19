package io.machinecode.chainlink.core.validation.visitor;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
final class Transition implements Serializable {
    private static final long serialVersionUID = 1L;

    Transition(final Type type, final String fromElement, final String from, final String toElement, final String to) {
        this.type = type;
        this.fromElement = fromElement;
        this.from = from;
        this.toElement = toElement;
        this.to = to;
    }

    final Type type;
    final String fromElement;
    final String from;
    final String toElement;
    final String to;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Transition)) return false;

        final Transition that = (Transition) o;

        if (type != that.type) return false;
        if (!from.equals(that.from)) return false;
        if (!fromElement.equals(that.fromElement)) return false;
        if (!to.equals(that.to)) return false;
        if (!toElement.equals(that.toElement)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromElement.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + toElement.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    public enum Type { CHILD, SIBLING }
}
