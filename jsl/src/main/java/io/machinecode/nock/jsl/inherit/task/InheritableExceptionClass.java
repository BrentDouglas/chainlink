package io.machinecode.nock.jsl.inherit.task;

import io.machinecode.nock.jsl.inherit.Util;
import io.machinecode.nock.spi.Mergeable;
import io.machinecode.nock.spi.element.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
            _this.setClassName(Util.attributeRule(_this.getClassName(), that.getClassName()));
            return _this;
        }
    }
}
