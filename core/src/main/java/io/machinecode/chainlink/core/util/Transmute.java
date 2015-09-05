/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.util;

import java.util.List;
import java.util.ListIterator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public final class Transmute {

    public static <V, X extends Mutable<V>> X item(final X to, final V from, final Creator<X> creator, final Op... ops) throws Exception {
        if (from == null) {
            if (Op.REMOVE.isActive(ops)) {
                return null;
            }
        } else if (to == null) {
            if (Op.ADD.isActive(ops)) {
                final X x = creator.create();
                x.accept(from);
                return x;
            }
        } else {
            if (Op.UPDATE.isActive(ops)) {
                to.accept(from);
            }
        }
        return to;
    }

    public static <V, X extends Mutable<V>> List<X> list(final List<X> to, final List<? extends V> from, final Creator<X> creator, final Op... ops) throws Exception {
        final ListIterator<X> it = to.listIterator();
        outer: while (it.hasNext()) {
            final X t = it.next();
            for (final V f : from) {
                if (t.willAccept(f)) {
                    if (Op.UPDATE.isActive(ops)) {
                        t.accept(f);
                    }
                    continue outer;
                }
            }
            if (Op.REMOVE.isActive(ops)) {
                it.remove();
            }
        }
        outer: for (final V f : from) {
            for (final X t : to) {
                if (t.willAccept(f)) {
                    if (Op.UPDATE.isActive(ops)) {
                        t.accept(f);
                    }
                    continue outer;
                }
            }
            if (Op.ADD.isActive(ops)) {
                final X x = creator.create();
                x.accept(f);
                to.add(x);
            }
        }
        return to;
    }

    public static <V,X> List<X> list(final List<X> to, final List<? extends V> from, final Copy.Transformer<V,X> transformer, final Op... ops) throws Exception {
        final ListIterator<X> it = to.listIterator();
        outer: while (it.hasNext()) {
            final X t = it.next();
            for (final V f : from) {
                if (t.equals(transformer.transform(f))) {
                    continue outer;
                }
            }
            if (Op.REMOVE.isActive(ops)) {
                it.remove();
            }
        }
        outer: for (final V f : from) {
            for (final X t : to) {
                if (t.equals(transformer.transform(f))) {
                    continue outer;
                }
            }
            if (Op.ADD.isActive(ops)) {
                to.add(transformer.transform(f));
            }
        }
        return to;
    }
}
