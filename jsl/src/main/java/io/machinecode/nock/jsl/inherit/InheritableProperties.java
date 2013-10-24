package io.machinecode.nock.jsl.inherit;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.Property;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableProperties<T extends InheritableProperties<T, P>,
        P extends Copyable<P> & Property>
        extends MergeableList<T>, Properties {

    T setPartition(final String partition);

    @Override
    List<P> getProperties();

    T setProperties(final List<P> properties);

    class PropertiesTool {

        public static <T extends InheritableProperties<T, P>,
                P extends Copyable<P> & Property>
        T copy(final T _this, final T that) {
            that.setProperties(Util.copyList(_this.getProperties()));
            return that;
        }

        public static <T extends InheritableProperties<T, P>,
                P extends Copyable<P> & Property>
        T merge(final T _this, final T that) {
            if (_this.getMerge()) {
                _this.setProperties(Util.listRule(_this.getProperties(), that.getProperties()));
            }
            return _this;
        }
    }
}
