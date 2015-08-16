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
package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableScopeSchema<Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Prop>>
                extends ScopeSchema<Prop,Job> {

    void setRef(final String ref);

    void setConfigurationLoaders(final List<String> artifactLoaders);

    void setJobOperators(final List<Job> jobOperators);

    void setProperties(final List<Prop> properties);

    Job removeJobOperator(final String name) throws NoJobOperatorWithNameException;

    /**
     * @param jobOperator
     * @throws JobOperatorWithNameExistsException
     * @throws Exception
     */
    void addJobOperator(final JobOperatorSchema<?> jobOperator) throws Exception;
}
