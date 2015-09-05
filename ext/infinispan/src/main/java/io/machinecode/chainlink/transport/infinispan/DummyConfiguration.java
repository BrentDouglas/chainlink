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
package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class DummyConfiguration implements Configuration {
    @Override
    public io.machinecode.chainlink.spi.execution.Executor getExecutor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TransactionManager getTransactionManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Marshalling getMarshalling() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MBeanServer getMBeanServer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JobLoader getJobLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Security getSecurity() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Repository getRepository() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Transport getTransport() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Registry getRegistry() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InjectionContext getInjectionContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProperty(final String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
