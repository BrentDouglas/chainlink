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
package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.core.expression.PropertyContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Copy {

    private static final Transformer<?,?> NOOP = new Transformer<Object, Object>() {
        @Override
        public Object transform(final Object that) {
            return that;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Transformer<T,T> noop() {
        return (Transformer<T,T>)NOOP;
    }

    public static <T, U, V extends PropertyContext> List<U> immutableCopy(final List<? extends T> that, final V context, final ExpressionTransformer<T, U, V> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value, context);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U> List<U> copy(final List<? extends T> that, final Transformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return list;
    }

    public interface Transformer<T, U> {
        U transform(final T that);
    }

    public interface ExpressionTransformer<T, U, V extends PropertyContext> {
        U transform(final T that, final V context);
    }
}
