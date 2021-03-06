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
package io.machinecode.chainlink.core.schema;

import io.machinecode.chainlink.core.util.Mutable;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableJobOperatorSchema<Prop extends MutablePropertySchema>
        extends JobOperatorSchema<Prop>, Mutable<JobOperatorSchema<?>> {

    void setName(final String name);

    void setRef(final String ref);

    void setClassLoader(final String classLoader);

    void setTransactionManager(final String transactionManager);

    void setMarshalling(final String marshalling);

    void setMBeanServer(final String mBeanServer);

    void setJobLoaders(final List<String> jobLoaders);

    void setArtifactLoaders(final List<String> artifactLoaders);

    void setSecurities(final List<String> securities);

    void setRepository(final String repository);

    void setRegistry(final String registry);

    void setTransport(final String transport);

    void setExecutor(final String executor);

    void setProperties(final List<Prop> properties);

    void setProperty(final String name, final String value);
}
