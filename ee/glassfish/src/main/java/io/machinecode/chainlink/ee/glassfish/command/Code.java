package io.machinecode.chainlink.ee.glassfish.command;

import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;

import java.beans.PropertyVetoException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public abstract class Code<T extends ConfigBeanProxy> implements SingleConfigCode<T> {
    @Override
    public final Object run(final T param) throws PropertyVetoException, TransactionFailure {
        try {
            code(param);
        } catch (final PropertyVetoException | TransactionFailure e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public abstract Object code(final T that) throws Exception;
}
