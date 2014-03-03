package io.machinecode.nock.core.model;

import io.machinecode.nock.core.impl.InjectablesImpl;
import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.Injectables;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.loader.ArtifactOfWrongTypeException;
import io.machinecode.nock.spi.util.Messages;
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
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl implements Listener, PropertyReference {

    private static final Logger log = Logger.getLogger(ListenerImpl.class);

    protected final PropertiesImpl properties;
    protected final ArtifactReferenceImpl ref;

    public ListenerImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
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

    protected Injectables _injectables(final ExecutionContext context) {
        if (this._injectables == null) {
            this._injectables = new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties()
            );
        }
        return this._injectables;
    }

    protected <T> T load(final Class<T> clazz, final InjectionContext injectionContext, final ExecutionContext context) throws Exception {
        if (this._cached != null) {
            if (clazz.isAssignableFrom(this._cached.getClass())) {
                return clazz.cast(this._cached);
            }
            throw new ArtifactOfWrongTypeException(Messages.format("NOCK-025000.artifact.loader.assignability", this.ref.ref(), clazz.getCanonicalName()));
        }
        final T that = this.ref.load(clazz, injectionContext, context);
        this._cached = that;
        return that;
    }

    public void beforeChunk(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ChunkListener.class, injectionContext, context).beforeChunk();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onError(final Executor executor, final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ChunkListener.class, injectionContext, context).onError(exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterChunk(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ChunkListener.class, injectionContext, context).afterChunk();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforeProcess(final Executor executor, final ExecutionContext context, final Object item) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemProcessListener.class, injectionContext, context).beforeProcess(item);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterProcess(final Executor executor, final ExecutionContext context, final Object item, final Object result) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
        load(ItemProcessListener.class, injectionContext, context).afterProcess(item, result);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onProcessError(final Executor executor, final ExecutionContext context, final Object item, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemProcessListener.class, injectionContext, context).onProcessError(item, exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforeRead(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemReadListener.class, injectionContext, context).beforeRead();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterRead(final Executor executor, final ExecutionContext context, final Object item) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemReadListener.class, injectionContext, context).afterRead(item);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onReadError(final Executor executor, final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemReadListener.class, injectionContext, context).onReadError(exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforeWrite(final Executor executor, final ExecutionContext context, final List<Object> items) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemWriteListener.class, injectionContext, context).beforeWrite(items);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterWrite(final Executor executor, final ExecutionContext context, final List<Object> items) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemWriteListener.class, injectionContext, context).afterWrite(items);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onWriteError(final Executor executor, final ExecutionContext context, final List<Object> items, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(ItemWriteListener.class, injectionContext, context).onWriteError(items, exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforeJob(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(JobListener.class, injectionContext, context).beforeJob();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterJob(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(JobListener.class, injectionContext, context).afterJob();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onRetryProcessException(final Executor executor, final ExecutionContext context, final Object item, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(RetryProcessListener.class, injectionContext, context).onRetryProcessException(item, exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onRetryReadException(final Executor executor, final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(RetryReadListener.class, injectionContext, context).onRetryReadException(exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onRetryWriteException(final Executor executor, final ExecutionContext context, final List<Object> items, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(RetryWriteListener.class, injectionContext, context).onRetryWriteException(items, exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onSkipProcessItem(final Executor executor, final ExecutionContext context, final Object item, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(SkipProcessListener.class, injectionContext, context).onSkipProcessItem(item, exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onSkipReadItem(final Executor executor, final ExecutionContext context, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(SkipReadListener.class, injectionContext, context).onSkipReadItem(exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void onSkipWriteItem(final Executor executor, final ExecutionContext context, final List<Object> items, final Exception exception) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(SkipWriteListener.class, injectionContext, context).onSkipWriteItem(items, exception);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforeStep(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(StepListener.class, injectionContext, context).beforeStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterStep(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(StepListener.class, injectionContext, context).afterStep();
        } finally {
            provider.setInjectables(null);
        }
    }
}
