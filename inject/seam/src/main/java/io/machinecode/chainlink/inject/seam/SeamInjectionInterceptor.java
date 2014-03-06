package io.machinecode.chainlink.inject.seam;

import io.machinecode.chainlink.inject.core.DefaultInjector;
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
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Name("seamInjectionInterceptor")
@Scope(ScopeType.STATELESS)
@Interceptor(stateless = true)
public class SeamInjectionInterceptor extends AbstractInterceptor {

    private final InjectablesProvider provider;

    public SeamInjectionInterceptor() {
        final ServiceLoader<InjectablesProvider> providers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<InjectablesProvider>>() {
            public ServiceLoader<InjectablesProvider> run() {
                return ServiceLoader.load(InjectablesProvider.class);
            }
        });
        final Iterator<InjectablesProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            provider = iterator.next();
        } else {
            throw new IllegalStateException(Messages.format("CHAINLINK-000000.injector.provider.unavailable"));
        }
    }

    @Override
    public boolean isInterceptorEnabled() {
        return true;
    }

    @AroundInvoke
    public Object aroundInvoke(final InvocationContext invocation) throws Exception {
        final Object bean = invocation.getTarget();
        DefaultInjector.doInject(provider, bean);
        return invocation.proceed();
    }
}
