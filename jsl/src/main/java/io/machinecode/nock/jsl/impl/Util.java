package io.machinecode.nock.jsl.impl;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Util {

    public static <T> List<T> immutableCopy(final List<T> that) {
        return Collections.unmodifiableList(new ArrayList<T>(that));
    }

    public static <T> Set<T> immutableCopy(final Set<T> that) {
        return Collections.unmodifiableSet(new THashSet<T>(that));
    }
}
