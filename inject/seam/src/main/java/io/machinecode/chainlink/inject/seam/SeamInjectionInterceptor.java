package io.machinecode.chainlink.inject.seam;

import io.machinecode.chainlink.inject.core.DefaultInjector;
import io.machinecode.chainlink.inject.core.LoadProviders;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;

import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@Name("seamInjectionInterceptor")
@Scope(ScopeType.STATELESS)
@Interceptor(stateless = true)
public class SeamInjectionInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;

    private transient InjectablesProvider provider;

    public SeamInjectionInterceptor() {
        this.provider = loadProvider();
    }

    private InjectablesProvider loadProvider() {
        final ServiceLoader<InjectablesProvider> providers = AccessController.doPrivileged(new LoadProviders());
        final Iterator<InjectablesProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            throw new IllegalStateException(Messages.format("CHAINLINK-000000.injector.provider.unavailable"));
        }
    }

    private InjectablesProvider _provider() {
        if (this.provider != null) {
            return this.provider;
        }
        return this.provider = loadProvider();
    }

    @Override
    public boolean isInterceptorEnabled() {
        return true;
    }

    @AroundInvoke
    public Object aroundInvoke(final InvocationContext invocation) throws Exception {
        final Object bean = invocation.getTarget();
        DefaultInjector.doInject(_provider(), bean);
        return invocation.proceed();
    }
}
