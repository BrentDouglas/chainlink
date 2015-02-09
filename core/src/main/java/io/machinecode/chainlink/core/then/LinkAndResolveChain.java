package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnResolve;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class LinkAndResolveChain implements OnResolve<Chain<?>> {
    private final Chain<?> chain;

    public LinkAndResolveChain(final Chain<?> chain) {
        this.chain = chain;
    }

    @Override
    public void resolve(final Chain<?> that) {
        chain.linkAndResolve(null, that != null ? that : new ResolvedChain<Void>(null));
    }
}
