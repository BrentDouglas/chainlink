package io.machinecode.nock.core.expression;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyResolver {

    String prefix();

    int length();

    CharSequence resolve(CharSequence value);
}
