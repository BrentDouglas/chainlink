package io.machinecode.nock.jsl.impl.chunk;

import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.api.chunk.Classes;

import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ClassesImpl implements Classes {

    private final Set<String> classes;

    public ClassesImpl(final Classes that) {
        this.classes = Util.immutableCopy(that.getClasses());
    }

    @Override
    public Set<String> getClasses() {
        return this.classes;
    }
}
