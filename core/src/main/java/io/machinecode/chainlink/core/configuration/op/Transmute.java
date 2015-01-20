package io.machinecode.chainlink.core.configuration.op;

import io.machinecode.chainlink.spi.management.Mutable;
import io.machinecode.chainlink.spi.management.Op;

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
}
