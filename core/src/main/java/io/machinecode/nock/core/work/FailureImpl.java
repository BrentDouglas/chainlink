package io.machinecode.nock.core.work;

import io.machinecode.nock.jsl.util.ImmutablePair;
import io.machinecode.nock.spi.transport.Failure;
import io.machinecode.nock.spi.transport.Transport;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FailureImpl implements Failure {

    private ImmutablePair<Transport, ExecutableImpl> always = null;

    public synchronized FailureImpl always(final Transport transport, final ExecutableImpl executable) {
        always = ImmutablePair.of(transport, executable);
        return this;
    }

    @Override
    public void fail(final Transport transport, final Exception exception) {
        doFail(transport, exception);
        if (always != null) {
            always.getKey().executeOnAnyThread(always.getValue());
        }
    }

    public abstract void doFail(final Transport transport, final Exception exception);
}
