package io.machinecode.chainlink.core.jsl.fluent;


import io.machinecode.chainlink.spi.jsl.inherit.InheritableListeners;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentListeners extends FluentMergeableList<FluentListeners> implements InheritableListeners<FluentListeners, FluentListener> {

    private boolean merge;
    private List<FluentListener> listeners = new ArrayList<>(0);

    @Override
    public List<FluentListener> getListeners() {
        return this.listeners;
    }

    @Override
    public FluentListeners setListeners(final List<FluentListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    public FluentListeners addListener(final FluentListener listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public boolean getMerge() {
        return merge;
    }

    @Override
    public FluentListeners setMerge(final boolean merge) {
        this.merge = merge;
        return this;
    }

    @Override
    public FluentListeners copy() {
        return copy(new FluentListeners());
    }

    @Override
    public FluentListeners copy(final FluentListeners that) {
        return ListenersTool.copy(this, that);
    }

    @Override
    public FluentListeners merge(final FluentListeners that) {
        return ListenersTool.merge(this, that);
    }
}
