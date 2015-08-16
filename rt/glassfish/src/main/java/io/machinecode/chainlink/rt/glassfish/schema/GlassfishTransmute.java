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

import io.machinecode.chainlink.core.util.Creator;
import io.machinecode.chainlink.rt.glassfish.command.BaseCommand;
import io.machinecode.chainlink.core.util.Op;
import org.jvnet.hk2.config.ConfigBeanProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public final class GlassfishTransmute {

    public static <V, X extends ConfigBeanProxy & Hack<V>> X item(final X to, final V from, final Creator<X> creator, final Op... ops) throws Exception {
        if (from == null) {
            if (Op.REMOVE.isActive(ops)) {
                return null;
            }
        } else if (to == null) {
            if (Op.ADD.isActive(ops)) {
                final X x = creator.create();
                BaseCommand.unlockedUpdate(x, from, ops);
                return x;
            }
        } else {
            if (Op.UPDATE.isActive(ops)) {
                BaseCommand.lockedUpdate(to, from, ops);
            }
        }
        return to;
    }

    public static <V, X extends ConfigBeanProxy & Hack<V>> List<X> list(final List<X> _to, final List<? extends V> from, final Creator<X> creator, final Op... ops) throws Exception {
        final List<X> to = new ArrayList<>(_to);
        final ListIterator<X> it = to.listIterator();
        outer: while (it.hasNext()) {
            final X t = it.next();
            for (final V f : from) {
                if (t.hack().willAccept(f)) {
                    if (Op.UPDATE.isActive(ops)) {
                        BaseCommand.lockedUpdate(t, f, ops);
                    }
                    continue outer;
                }
            }
            if (Op.REMOVE.isActive(ops)) {
                it.remove();
            }
        }
        outer: for (final V f : from) {
            for (final X t : to) {
                if (t.hack().willAccept(f)) {
                    if (Op.UPDATE.isActive(ops)) {
                        BaseCommand.lockedUpdate(t, f, ops);
                    }
                    continue outer;
                }
            }
            if (Op.ADD.isActive(ops)) {
                final X x = creator.create();
                BaseCommand.unlockedUpdate(x, f, ops);
                to.add(x);
            }
        }
        return to;
    }
}
