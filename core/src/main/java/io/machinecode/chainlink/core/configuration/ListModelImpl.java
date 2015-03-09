package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.ListModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class ListModelImpl<T> extends ArrayList<DeclarationImpl<T>> implements ListModel<T> {
    @Override
    public void set(final Collection<String> that) {
        clear();
        if (that == null) {
            return;
        }
        for (final String value : that) {
            add(value);
        }
    }

    @Override
    public void add(final String that) {
        if (that == null) {
            return;
        }
        final DeclarationImpl<T> dec = create();
        dec.setRef(that);
        super.add(dec);
    }

    @Override
    public Declaration<T> add() {
        final DeclarationImpl<T> dec = create();
        super.add(dec);
        return dec;
    }

    protected abstract DeclarationImpl<T> create();
}
