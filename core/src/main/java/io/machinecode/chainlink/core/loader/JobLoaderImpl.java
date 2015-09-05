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

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.spi.loader.JobLoader;
import org.jboss.logging.Logger;

import javax.batch.operations.NoSuchJobException;
import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobLoaderImpl implements JobLoader {

    private static final Logger log = Logger.getLogger(JobLoaderImpl.class);

    private final JarXmlJobLoader jarLoader;
    private final WarXmlJobLoader warLoader;
    private final Set<JobLoader> loaders;

    public JobLoaderImpl(final ClassLoader classLoader, final JobLoader... jobLoaders) throws JAXBException {
        this.jarLoader = new JarXmlJobLoader(classLoader);
        this.warLoader = new WarXmlJobLoader(classLoader);
        this.loaders = new TLinkedHashSet<>();
        Collections.addAll(this.loaders, jobLoaders);
    }

    @Override
    public Job load(final String jslName) throws NoSuchJobException {
        // 1. Provided Loaders
        for (final JobLoader loader : this.loaders) {
            try {
                return loader.load(jslName);
            } catch (final NoSuchJobException e) {
                log.tracef(Messages.get("CHAINLINK-003100.job.loader.not.found"), jslName, loader.getClass().getSimpleName());
            }
        }
        // 2. Archive Loaders
        try {
            return jarLoader.load(jslName);
        } catch (final NoSuchJobException e) {
            log.tracef(Messages.get("CHAINLINK-003100.job.loader.not.found"), jslName, jarLoader.getClass().getSimpleName());
        }
        return warLoader.load(jslName);
    }
}
