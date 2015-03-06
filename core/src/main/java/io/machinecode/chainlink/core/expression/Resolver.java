package io.machinecode.chainlink.core.expression;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
abstract class Resolver {
    protected static final String EMPTY = "";

    final String prefix;
    final int length;

    Resolver(final String prefix, final int length) {
        this.prefix = prefix;
        this.length = length;
    }

    abstract CharSequence resolve(final CharSequence value);
}
