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
package io.machinecode.chainlink.inject.seam;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.Injector;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Name("seamInjectionInterceptor")
@Scope(ScopeType.STATELESS)
@Interceptor(stateless = true)
public class SeamInjectionInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;

    private transient InjectablesProvider provider;

    public SeamInjectionInterceptor() {
        this.provider = ArtifactLoaderImpl.loadProvider();
    }
    private InjectablesProvider _provider() {
        if (this.provider != null) {
            return this.provider;
        }
        return this.provider = ArtifactLoaderImpl.loadProvider();
    }

    @Override
    public boolean isInterceptorEnabled() {
        return true;
    }

    @AroundInvoke
    public Object aroundInvoke(final InvocationContext invocation) throws Exception {
        final Object bean = invocation.getTarget();
        Injector.inject(_provider(), bean);
        return invocation.proceed();
    }
}
