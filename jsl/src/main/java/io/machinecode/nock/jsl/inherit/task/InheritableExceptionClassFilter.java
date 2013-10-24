package io.machinecode.nock.jsl.inherit.task;

import io.machinecode.nock.jsl.inherit.MergeableList;
import io.machinecode.nock.jsl.inherit.Util;
import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.task.ExceptionClass;
import io.machinecode.nock.spi.element.task.ExceptionClassFilter;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableExceptionClassFilter<T extends InheritableExceptionClassFilter<T, E>,
        E extends Copyable<E> & ExceptionClass>
        extends MergeableList<T>, ExceptionClassFilter {

    @Override
    List<E> getIncludes();

    T setIncludes(final List<E> includes);

    @Override
    List<E> getExcludes();

    T setExcludes(final List<E> excludes);

    class ExceptionClassFilterTool {

        public static <T extends InheritableExceptionClassFilter<T, E>,
                E extends Copyable<E> & ExceptionClass>
        T copy(final T _this, final T that) {
            that.setIncludes(Util.copyList(_this.getIncludes()));
            that.setExcludes(Util.copyList(_this.getExcludes()));
            return that;
        }

        public static <T extends InheritableExceptionClassFilter<T, E>,
                E extends Copyable<E> & ExceptionClass>
        T merge(final T _this, final T that) {
            if (_this.getMerge()) {
                _this.setIncludes(Util.listRule(_this.getIncludes(), that.getIncludes()));
                _this.setExcludes(Util.listRule(_this.getExcludes(), that.getExcludes()));
            }
            return _this;
        }
    }
}
