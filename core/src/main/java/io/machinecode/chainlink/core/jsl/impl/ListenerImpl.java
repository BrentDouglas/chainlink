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
package io.machinecode.chainlink.core.jsl.impl;

import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.Listener;
import io.machinecode.chainlink.spi.jsl.PropertyReference;
import org.jboss.logging.Logger;

import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ListenerImpl implements Listener, PropertyReference, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ListenerImpl.class);

    protected final PropertiesImpl properties;
    protected final ArtifactReference ref;

    public ListenerImpl(final ArtifactReference ref, final PropertiesImpl properties) {
        this.ref = ref;
        this.properties = properties;
    }

    @Override
    public String getRef() {
        return this.ref.ref();
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    private transient Injectables _injectables;
    protected transient Object _cached;

    protected Injectables _injectables(final Configuration configuration, final ExecutionContext context) {
        if (this._injectables == null) {
            this._injectables = new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties(),
                    configuration.getRegistry().getOrCreateScope(context)
            );
        }
        return this._injectables;
    }

    protected <T> T load(final Class<T> clazz, final InjectionContext injectionContext, final ExecutionContext context) throws Exception {
        if (this._cached != null) {
            if (clazz.isAssignableFrom(this._cached.getClass())) {
                return clazz.cast(this._cached);
            }
            throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", this.ref.ref(), clazz.getCanonicalName()));
        }
        final T that = this.ref.load(clazz, injectionContext, context);
        this._cached = that;
        return that;
    }

    public void beforeChunk(final Configuration configuration,  final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ChunkListener.class, injectionContext, context).beforeChunk();
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onError(final Configuration configuration,  final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ChunkListener.class, injectionContext, context).onError(exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void afterChunk(final Configuration configuration,  final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ChunkListener.class, injectionContext, context).afterChunk();
        } finally {
            provider.releaseInjectables();
        }
    }

    public void beforeProcess(final Configuration configuration,  final ExecutionContext context, final Object item) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemProcessListener.class, injectionContext, context).beforeProcess(item);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void afterProcess(final Configuration configuration,  final ExecutionContext context, final Object item, final Object result) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
        load(ItemProcessListener.class, injectionContext, context).afterProcess(item, result);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onProcessError(final Configuration configuration,  final ExecutionContext context, final Object item, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemProcessListener.class, injectionContext, context).onProcessError(item, exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void beforeRead(final Configuration configuration,  final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemReadListener.class, injectionContext, context).beforeRead();
        } finally {
            provider.releaseInjectables();
        }
    }

    public void afterRead(final Configuration configuration,  final ExecutionContext context, final Object item) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemReadListener.class, injectionContext, context).afterRead(item);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onReadError(final Configuration configuration,  final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemReadListener.class, injectionContext, context).onReadError(exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void beforeWrite(final Configuration configuration,  final ExecutionContext context, final List<Object> items) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemWriteListener.class, injectionContext, context).beforeWrite(items);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void afterWrite(final Configuration configuration,  final ExecutionContext context, final List<Object> items) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemWriteListener.class, injectionContext, context).afterWrite(items);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onWriteError(final Configuration configuration,  final ExecutionContext context, final List<Object> items, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(ItemWriteListener.class, injectionContext, context).onWriteError(items, exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void beforeJob(final Configuration configuration,  final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(JobListener.class, injectionContext, context).beforeJob();
        } finally {
            provider.releaseInjectables();
        }
    }

    public void afterJob(final Configuration configuration,  final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(JobListener.class, injectionContext, context).afterJob();
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onRetryProcessException(final Configuration configuration,  final ExecutionContext context, final Object item, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(RetryProcessListener.class, injectionContext, context).onRetryProcessException(item, exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onRetryReadException(final Configuration configuration,  final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(RetryReadListener.class, injectionContext, context).onRetryReadException(exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onRetryWriteException(final Configuration configuration,  final ExecutionContext context, final List<Object> items, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(RetryWriteListener.class, injectionContext, context).onRetryWriteException(items, exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onSkipProcessItem(final Configuration configuration,  final ExecutionContext context, final Object item, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(SkipProcessListener.class, injectionContext, context).onSkipProcessItem(item, exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onSkipReadItem(final Configuration configuration,  final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(SkipReadListener.class, injectionContext, context).onSkipReadItem(exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void onSkipWriteItem(final Configuration configuration,  final ExecutionContext context, final List<Object> items, final Exception exception) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(SkipWriteListener.class, injectionContext, context).onSkipWriteItem(items, exception);
        } finally {
            provider.releaseInjectables();
        }
    }

    public void beforeStep(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(StepListener.class, injectionContext, context).beforeStep();
        } finally {
            provider.releaseInjectables();
        }
    }

    public void afterStep(final Configuration configuration,  final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(configuration, context));
            load(StepListener.class, injectionContext, context).afterStep();
        } finally {
            provider.releaseInjectables();
        }
    }
}
