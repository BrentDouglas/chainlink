package io.machinecode.chainlink.core.expression;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PropertyResolver {

    String prefix();

    int length();

    CharSequence resolve(CharSequence value);
}
