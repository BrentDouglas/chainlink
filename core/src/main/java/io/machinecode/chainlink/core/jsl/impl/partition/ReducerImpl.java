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
package io.machinecode.chainlink.core.jsl.impl.partition;

import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertyReferenceImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.partition.Reducer;

import javax.batch.api.partition.PartitionReducer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ReducerImpl extends PropertyReferenceImpl<PartitionReducer> implements Reducer {
    private static final long serialVersionUID = 1L;

    public ReducerImpl(final ArtifactReference ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void beginPartitionedStep(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).beginPartitionedStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforePartitionedStepCompletion(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).beforePartitionedStepCompletion();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void rollbackPartitionedStep(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).rollbackPartitionedStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterPartitionedStepCompletion(final Configuration configuration, final ExecutionContext context, final PartitionReducer.PartitionStatus status) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).afterPartitionedStepCompletion(status);
        } finally {
            provider.setInjectables(null);
        }
    }
}
