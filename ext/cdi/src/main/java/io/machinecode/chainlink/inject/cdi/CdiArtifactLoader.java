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
package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.ClosableScope;
import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(CdiArtifactLoader.class);

    private final BeanManagerLookup lookup;
    private BeanManager beanManager;

    private final InjectablesProvider provider;

    private CdiArtifactLoader(final BeanManagerLookup lookup, final BeanManager beanManager) {
        this.lookup = lookup;
        this.beanManager = beanManager;
        this.provider = ArtifactLoaderImpl.loadProvider();
    }

    public CdiArtifactLoader(final BeanManagerLookup lookup) {
        this(lookup, null);
    }

    public CdiArtifactLoader(final BeanManager beanManager) {
        this(null, beanManager);
    }

    public CdiArtifactLoader() {
        this(null, null);
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) throws Exception {
        if (this.beanManager == null) {
            this.beanManager = lookup.lookupBeanManager();
        }
        final Injectables injectables = provider.getInjectables();
        final T that = _inject(beanManager, as, id, injectables.getScope(), new NamedLiteral(id));
        if (that == null) {
            return null;
        }
        final String name = that.getClass().getName();
        if (!name.contains("_$$_Weld")) { //TODO Also check OWB
            Injector.inject(injectables, that);
        }
        return that;
    }

    @Produces
    @Dependent
    @BatchProperty
    public String getBatchProperty(final InjectionPoint injectionPoint) {
        final BatchProperty batchProperty = injectionPoint.getAnnotated().getAnnotation(BatchProperty.class);
        final Member field = injectionPoint.getMember();
        final String property = Injector.property(batchProperty.name(), field.getName(), provider.getInjectables().getProperties());
        if (property == null || "".equals(property)) {
            return null;
        }
        return property;
    }

    @Produces
    @Dependent
    @Default
    public JobContext getJobContext() {
        return provider.getInjectables().getJobContext();
    }

    @Produces
    @Dependent
    @Default
    public StepContext getStepContext() {
        return provider.getInjectables().getStepContext();
    }

    static <T> T _inject(final BeanManager beanManager, final Class<T> as, final String id, final ClosableScope scope, final Annotation... annotation) {
        final Set<Bean<?>> beans = beanManager.getBeans(as, annotation);
        final Bean<?> bean = beanManager.resolve(beans);
        if (bean == null) {
            if (id != null && !beanManager.getBeans(id).isEmpty()) {
                throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getSimpleName()));
            }
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        if (!beanManager.isNormalScope(bean.getScope())) {
            scope.onClose(new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    ctx.release();
                }
            });
        }
        return as.cast(beanManager.getReference(bean, as, ctx));
    }
}
