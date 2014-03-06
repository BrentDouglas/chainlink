package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.Property;

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
            that.setPartition(_this.getPartition());
            that.setProperties(Util.copyList(_this.getProperties()));
            return that;
        }

        public static <T extends InheritableProperties<T, P>,
                P extends Copyable<P> & Property>
        T merge(final T _this, final T that) {
            _this.setPartition(Util.attributeRule(_this.getPartition(), that.getPartition()));
            if (_this.getMerge()) {
                _this.setProperties(Util.listRule(_this.getProperties(), that.getProperties()));
            }
            return _this;
        }
    }
}
