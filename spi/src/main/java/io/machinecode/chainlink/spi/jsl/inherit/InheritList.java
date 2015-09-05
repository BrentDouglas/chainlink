/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InheritList<T extends Inheritable<T> & Copyable<T>> extends ForwardingList<T> {
    private static final long serialVersionUID = 1L;

    public InheritList(final InheritableJobLoader repository, final String defaultJobXml, final List<? extends T> delegate) {
        super(new ArrayList<T>(delegate.size()));
        for (final T that : delegate) {
            if (that instanceof InheritableElement) {
                final InheritableElement inheritable = (InheritableElement)that;
                if (inheritable.isAbstract() != null && inheritable.isAbstract()) {
                    continue;
                }
            }
            this.delegate.add(that.inherit(repository, defaultJobXml));
        }
    }
}
