package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.InheritableElement;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableBase<T extends InheritableBase<T>>
        extends InheritableElement<T> {

    T setAbstract(Boolean _abstract);

    T setParent(String parent);

    T setJslName(String jslName);

    /**
     * @author Brent Douglas <brent.n.douglas@gmail.com>
     */
    class BaseTool {

        public static <T extends InheritableBase<T>>
        T copy(final T _this, final T that) {
            that.setAbstract(_this.isAbstract());
            that.setParent(_this.getParent());
            that.setJslName(_this.getJslName());
            return that;
        }

        /**
         * Pulls attributes from the parent elementName.
         *
         * @param parent
         */
        public static void inheritingElementRule(final InheritableBase _this, final InheritableBase parent) {
            parent.setAbstract(null);
            _this.setParent(null);
            _this.setJslName(null);
        }
    }
}
