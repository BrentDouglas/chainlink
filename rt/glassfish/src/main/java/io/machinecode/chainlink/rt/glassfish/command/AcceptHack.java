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
package io.machinecode.chainlink.rt.glassfish.command;

import io.machinecode.chainlink.rt.glassfish.schema.Hack;
import io.machinecode.chainlink.core.util.Op;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

import java.beans.PropertyVetoException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class AcceptHack<F, T extends Hack<F> & ConfigBeanProxy> implements SingleConfigCode<T> {
    private final F from;
    private final Op[] ops;

    public AcceptHack(final F from, final Op... ops) {
        this.from = from;
        this.ops = ops;
    }

    @Override
    public Object run(final T to) throws PropertyVetoException, TransactionFailure {
        try {
            to.hack().accept(this.from, ops);
        } catch (final PropertyVetoException | TransactionFailure e) {
            throw e;
        } catch (final Exception e) {
            throw new TransactionFailure(e.getMessage(), e);
        }
        return null;
    }
}
