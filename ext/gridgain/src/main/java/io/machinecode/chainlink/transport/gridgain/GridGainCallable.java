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
package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.core.transport.cmd.Command;
import org.gridgain.grid.Grid;
import org.gridgain.grid.resources.GridInstanceResource;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainCallable<T> implements Callable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    final Command<T> command;
    final UUID origin;

    @GridInstanceResource
    private transient Grid grid;

    public GridGainCallable(final Command<T> command, final UUID origin) {
        this.command = command;
        this.origin = origin;
    }

    @Override
    public T call() throws Exception {
        try {
            final Configuration configuration = this.grid.<String, Configuration>nodeLocalMap().get(Configuration.class.getName());
            return command.perform(configuration, origin);
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Exception(e);
        }
    }
}
