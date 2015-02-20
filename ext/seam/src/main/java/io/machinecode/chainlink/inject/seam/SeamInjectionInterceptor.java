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
