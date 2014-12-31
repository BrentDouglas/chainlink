package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.PropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritablePropertyReference<T extends InheritablePropertyReference<T, P>,
        P extends Mergeable<P> & Properties>
        extends Mergeable<T>, PropertyReference {

    T setRef(final String ref);

    @Override
    P getProperties();

    T setProperties(final P properties);

    class PropertyReferenceTool {

        public static <T extends InheritablePropertyReference<T, P>,
                P extends Mergeable<P> & Properties>
        T copy(final T _this, final T that) {
            that.setRef(_this.getRef());
            that.setProperties(Rules.copy(_this.getProperties()));
            return that;
        }

        public static <T extends InheritablePropertyReference<T, P>,
                P extends Mergeable<P> & Properties>
        T merge(final T _this, final T that) {
            _this.setRef(Rules.attributeRule(_this.getRef(), that.getRef()));
            _this.setProperties(Rules.merge(_this.getProperties(), that.getProperties()));
            return _this;
        }
    }

}
