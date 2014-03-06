package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.Inheritable;
import io.machinecode.chainlink.spi.loader.JobRepository;
import io.machinecode.chainlink.spi.Mergeable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Util {

    public static <X extends Copyable<X>> X copy(final X that) {
        return that == null ? null : that.copy();
    }
    public static <X extends Copyable<X>> CopyList<X> copyList(final List<? extends X> that) {
        return that == null ? null : new CopyList<X>(that);
    }
    public static <X extends Copyable<X>> CopyList<X> copyList(final List<? extends X>... that) {
        return that == null ? null : new CopyList<X>(that);
    }
    public static <X extends Inheritable<X>> InheritList<X> inheritingList(final JobRepository repository, final String defaultJobXml, final List<? extends X> that) {
        return that == null ? null : new InheritList<X>(repository, defaultJobXml, that);
    }

    public static <X extends Mergeable<X>> X merge(final X child, final X parent) {
        if (child == null) {
            return parent;
        }
        if (parent == null) {
            return child;
        }
        return child.merge(parent);
    }

    public static <X> X attributeRule(final X child, final X parent) {
        if (child == null) {
            return parent;
        }
        return child;
    }

    public static <X extends Copyable<X>> X elementRule(final X child, final X parent) {
        if (child == null) {
            return parent == null ? null : parent.copy();
        }
        return child.copy();
    }

    //TODO This needs to call merge
    public static <X extends Mergeable<X>> X recursiveElementRule(final X child, final X parent, final JobRepository repository, final String defaultJobXml) {
        if (child == null) {
            return parent == null ? null : parent.copy();
        }
        final X that = child.copy();
        if (that instanceof InheritableBase) {
            ((Inheritable)that).inherit(repository, defaultJobXml);
        }
        return that.merge(parent);
    }

    public static <X extends Copyable<X>> List<X> listRule(final List<X> child, final List<X> parent) {
        if (child == null) {
            return new CopyList<X>(parent);
        }
        if (parent == null) {
            return new CopyList<X>(child);
        }
        return new CopyList<X>(parent, child);
    }

    public static <X> List<X> listRule2(final List<X> child, final List<X> parent) {
        if (child == null) {
            return new ArrayList<X>(parent);
        }
        if (parent == null) {
            return new ArrayList<X>(child);
        }
        final List<X> that = new ArrayList<X>(parent);
        that.addAll(child);
        return that;
    }
}
