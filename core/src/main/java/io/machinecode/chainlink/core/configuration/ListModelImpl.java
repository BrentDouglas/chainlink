/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
