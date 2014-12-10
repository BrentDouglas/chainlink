package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.Property;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableProperty<T extends InheritableProperty<T>>
        extends Copyable<T>, Property {

    T setName(final String name);

    T setValue(final String value);

    class PropertyTool {

        public static <T extends InheritableProperty<T>>
        T copy(final T _this, final T that) {
            that.setName(_this.getName());
            that.setValue(_this.getValue());
            return that;
        }
    }
}
