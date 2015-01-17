package io.machinecode.chainlink.ee.glassfish.command;

import io.machinecode.chainlink.core.configuration.op.Op;
import io.machinecode.chainlink.ee.glassfish.configuration.Hack;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

import java.beans.PropertyVetoException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class AcceptHack<F, T extends Hack<F> & ConfigBeanProxy> implements SingleConfigCode<T> {
    private final F from;
    private final Op[] ops;

    public AcceptHack(final F from, final Op... ops) {
        this.from = from;
        this.ops = ops;
    }

    @Override
    public Object run(final T to) throws PropertyVetoException, TransactionFailure {
        try {
            to.hack().accept(this.from, ops);
        } catch (final PropertyVetoException | TransactionFailure e) {
            throw e;
        } catch (final Exception e) {
            throw new TransactionFailure(e.getMessage(), e);
        }
        return null;
    }
}
