package io.machinecode.chainlink.spi.jsl.inherit.task;

import io.machinecode.chainlink.spi.jsl.inherit.Mergeable;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClass;
import io.machinecode.chainlink.spi.jsl.inherit.Rules;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableExceptionClass<T extends InheritableExceptionClass<T>>
        extends Mergeable<T>, ExceptionClass {

    T setClassName(final String className);

    class ExceptionClassTool {

        public static <T extends InheritableExceptionClass<T>>
        T copy(final T _this, final T that) {
            that.setClassName(_this.getClassName());
            return that;
        }

        public static <T extends InheritableExceptionClass<T>>
        T merge(final T _this, final T that) {
            _this.setClassName(Rules.attributeRule(_this.getClassName(), that.getClassName()));
            return _this;
        }
    }
}
