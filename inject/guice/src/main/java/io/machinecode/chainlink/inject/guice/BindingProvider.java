package io.machinecode.chainlink.inject.guice;

import io.machinecode.chainlink.jsl.core.util.Triplet;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface BindingProvider {

    List<Triplet<Class<?>, String, Class<?>>> getBindings();
}
