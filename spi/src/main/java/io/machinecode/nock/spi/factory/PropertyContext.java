package io.machinecode.nock.spi.factory;

import io.machinecode.nock.spi.util.Pair;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyContext {

    List<? extends Pair<String, String>> getProperties();
}
