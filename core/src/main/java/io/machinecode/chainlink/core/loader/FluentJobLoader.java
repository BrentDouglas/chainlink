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
package io.machinecode.chainlink.core.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.inherit.InheritableJob;

import javax.batch.operations.NoSuchJobException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentJobLoader extends AbstractJobLoader {

    public FluentJobLoader add(final String jslName, final InheritableJob<?,?,?,?> job) {
        repos.put(jslName, new Node(jslName, job));
        return this;
    }

    public FluentJobLoader add(final InheritableJob<?,?,?,?> job) {
        final String jslName = job.getId();
        repos.put(jslName, new Node(jslName, job));
        return this;
    }

    final TMap<String, Node> repos = new THashMap<>();

    @Override
    protected Node doLoad(final String jslName) throws NoSuchJobException {
        final Node cached = repos.get(jslName);
        if (cached != null) {
            return cached;
        }
        throw new NoSuchJobException(Messages.format("CHAINLINK-003000.job.loader.no.file", jslName));
    }
}
