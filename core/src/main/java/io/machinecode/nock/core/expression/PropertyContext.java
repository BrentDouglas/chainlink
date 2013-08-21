package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyContext {

    List<MutablePair<String, String>> getProperties();
}
