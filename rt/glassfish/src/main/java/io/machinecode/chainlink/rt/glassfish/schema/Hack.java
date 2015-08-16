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
package io.machinecode.chainlink.rt.glassfish.schema;

import io.machinecode.chainlink.core.util.Mutable;
import org.jvnet.hk2.config.DuckTyped;

/**
 * This is only a thing as Glassfish's proxy wiring wont let a config bean
 * override an interface method with @DuckTyped.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Hack<T> {

    @DuckTyped
    Mutable<T> hack();

    class Duck {

        public static <T> Mutable<T> hack(final Hack<T> to) {
            final Class<?> clazz = to.getClass();
            for (final Class<?> interfaz : clazz.getInterfaces()) {
                if (interfaz.getSimpleName().startsWith("Glassfish")
                        && interfaz.getPackage().equals(Hack.class.getPackage())) {
                    try {
                        final Object duck = interfaz.getClassLoader()
                                    .loadClass(interfaz.getName() + "$Duck")
                                    .getConstructors()[0]
                                    .newInstance(to);
                        return (Mutable<T>)duck;
                    } catch (final RuntimeException e) {
                        throw e;
                    } catch (final Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            throw new IllegalStateException();
        }
    }
}
