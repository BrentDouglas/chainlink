package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.Property;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
            that.setProperties(Rules.copyList(_this.getProperties()));
            return that;
        }

        public static <T extends InheritableProperties<T, P>,
                P extends Copyable<P> & Property>
        T merge(final T _this, final T that) {
            _this.setPartition(Rules.attributeRule(_this.getPartition(), that.getPartition()));
            if (_this.getMerge()) {
                _this.setProperties(Rules.listRule(_this.getProperties(), that.getProperties()));
            }
            return _this;
        }
    }
}
