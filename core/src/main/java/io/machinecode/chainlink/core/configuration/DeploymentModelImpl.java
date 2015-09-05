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
package io.machinecode.chainlink.core.configuration;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.schema.xml.XmlChainlink;
import io.machinecode.chainlink.spi.configuration.DeploymentModel;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeploymentModelImpl extends ScopeModelImpl implements DeploymentModel {

    public DeploymentModelImpl(final SubSystemModelImpl parent) {
        super(parent);
    }

    private DeploymentModelImpl(final DeploymentModelImpl that, final ClassLoader loader) {
        super(new WeakReference<>(loader), new THashSet<>(that.names), that.parent);
        for (final Map.Entry<String, JobOperatorModelImpl> entry : that.jobOperators.entrySet()) {
            this.jobOperators.put(entry.getKey(), entry.getValue().copy(this));
        }
    }

    public DeploymentModelImpl copy(final ClassLoader loader) {
        return new DeploymentModelImpl(this, loader);
    }

    public DeploymentModel loadChainlinkXml() throws Exception {
        final ClassLoader loader = this.loader.get();
        if (loader == null) {
            throw new IllegalStateException(); //TODO Message
        }
        final String chainlinkXml = System.getProperty(Constants.CHAINLINK_XML, Constants.Defaults.CHAINLINK_XML);
        final InputStream stream = loader.getResourceAsStream(chainlinkXml);
        if (stream != null) {
            Model.configureDeployment(this, XmlChainlink.read(stream), loader);
        }
        return this;
    }

    public DeploymentModel loadChainlinkXml(final InputStream stream) throws Exception {
        Model.configureDeployment(this, XmlChainlink.read(stream), loader.get());
        return this;
    }
}
