package io.machinecode.nock.jsl.fluent.chunk;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.jsl.api.chunk.Classes;

import java.util.Collections;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentClasses implements Classes {

    private final Set<String> classes = new THashSet<String>(0);

    @Override
    public Set<String> getClasses() {
        return this.classes;
    }

    public FluentClasses addClasses(final String... classes) {
        Collections.addAll(this.classes, classes);
        return this;
    }

    public FluentClasses addClass(final String clazz) {
        this.classes.add(clazz);
        return this;
    }
}
