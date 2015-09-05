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
package io.machinecode.chainlink.rt.glassfish.schema;

import io.machinecode.chainlink.core.schema.xml.XmlSchema;
import io.machinecode.chainlink.core.util.Creator;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.JobOperatorWithNameExistsException;
import io.machinecode.chainlink.core.schema.MutableDeploymentSchema;
import io.machinecode.chainlink.core.schema.NoJobOperatorWithNameException;
import io.machinecode.chainlink.core.util.Strings;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Configured
public interface GlassfishDeployment extends ConfigBeanProxy, MutableDeploymentSchema<GlassfishProperty, GlassfishJobOperator>, Hack<DeploymentSchema<?,?>> {

    @Attribute("name")
    String getName();

    void setName(final String name);

    @Attribute("ref")
    String getRef();

    void setRef(final String ref);

    @Attribute("configuration-loaders")
    String getConfigurationLoadersString();

    @Attribute("configuration-loaders")
    void setConfigurationLoadersString(final String configurationLoader);

    @Element("job-operator")
    List<GlassfishJobOperator> getJobOperator();

    void setJobOperator(final List<GlassfishJobOperator> jobOperators);

    @Element("property")
    List<GlassfishProperty> getProperty();

    void setProperty(final List<GlassfishProperty> properties);

    @DuckTyped
    List<String> getConfigurationLoaders();

    @DuckTyped
    void setConfigurationLoaders(final List<String> artifactLoaders);

    @DuckTyped
    List<GlassfishJobOperator> getJobOperators();

    @DuckTyped
    void setJobOperators(final List<GlassfishJobOperator> jobOperators);

    @DuckTyped
    void setProperties(final List<GlassfishProperty> properties);

    @DuckTyped
    List<GlassfishProperty> getProperties();

    @DuckTyped
    GlassfishJobOperator getJobOperator(final String name);

    @DuckTyped
    GlassfishJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException;

    @DuckTyped
    void addJobOperator(final JobOperatorSchema<?> jobOperator) throws Exception;

    class Duck implements Mutable<DeploymentSchema<?,?>> {

        private final GlassfishDeployment to;

        public Duck(final GlassfishDeployment to) {
            this.to = to;
        }

        public static List<String> getConfigurationLoaders(final GlassfishDeployment that) {
            final String value = that.getConfigurationLoadersString();
            return value == null || value.isEmpty() ? Collections.<String>emptyList() : Strings.split(XmlSchema.XML_LIST_DELIMITER, value);
        }

        public static void setConfigurationLoaders(final GlassfishDeployment that, final List<String> configurationLoaders) {
            that.setConfigurationLoadersString(configurationLoaders == null || configurationLoaders.isEmpty() ? null : Strings.join(' ', configurationLoaders));
        }

        public static List<GlassfishJobOperator> getJobOperators(final GlassfishDeployment that) {
            return that.getJobOperator();
        }

        public static void setJobOperators(final GlassfishDeployment that, final List<GlassfishJobOperator> jobOperators) {
            that.setJobOperator(jobOperators);
        }

        public static List<GlassfishProperty> getProperties(final GlassfishDeployment that) {
            return that.getProperty();
        }

        public static void setProperties(final GlassfishDeployment that, final List<GlassfishProperty> properties) {
            that.setProperty(properties);
        }

        public static GlassfishJobOperator getJobOperator(final GlassfishDeployment self, final String name) {
            for (final GlassfishJobOperator dep : self.getJobOperators()) {
                if (name.equals(dep.getName())) {
                    return dep;
                }
            }
            return null;
        }

        public static GlassfishJobOperator removeJobOperator(final GlassfishDeployment self, final String name) throws NoJobOperatorWithNameException {
            final List<GlassfishJobOperator> jobOperators = new ArrayList<>(self.getJobOperators());
            final ListIterator<GlassfishJobOperator> it = jobOperators.listIterator();
            while (it.hasNext()) {
                final GlassfishJobOperator that = it.next();
                if (name.equals(that.getName())) {
                    it.remove();
                    self.setJobOperators(jobOperators);
                    return that;
                }
            }
            throw new NoJobOperatorWithNameException("No job operator with name " + name);
        }

        public static void addJobOperator(final GlassfishDeployment self, final JobOperatorSchema<?> jobOperator) throws Exception {
            if (getJobOperator(self, jobOperator.getName()) != null) {
                throw new JobOperatorWithNameExistsException("A job operator already exists with name " + jobOperator.getName());
            }
            final GlassfishJobOperator op = self.createChild(GlassfishJobOperator.class);
            op.accept(jobOperator);
            self.getJobOperators().add(op);
        }

        @Override
        public boolean willAccept(final DeploymentSchema<?,?> that) {
            return to.getName().equals(that.getName());
        }

        @Override
        public void accept(final DeploymentSchema<?,?> from, final Op... ops) throws Exception {
            to.setName(from.getName());
            to.setRef(from.getRef());
            to.setConfigurationLoaders(from.getConfigurationLoaders());
            to.setJobOperators(GlassfishTransmute.<JobOperatorSchema<?>, GlassfishJobOperator>list(to.getJobOperators(), from.getJobOperators(), new Creator<GlassfishJobOperator>() {
                @Override
                public GlassfishJobOperator create() throws Exception {
                    return to.createChild(GlassfishJobOperator.class);
                }
            }, ops));
        }
    }
}
