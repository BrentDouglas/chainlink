package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.then.ResolvedChain;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnResolve;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class LinkAndRejectChain implements OnResolve<Chain<?>> {
    private final Chain<?> chain;
    private final Throwable e;

    public LinkAndRejectChain(final Chain<?> chain, final Throwable e) {
        this.chain = chain;
        this.e = e;
    }

    @Override
    public void resolve(final Chain<?> that) {
        chain.linkAndReject(e, that != null ? that : new ResolvedChain<Void>(null));
    }
}
