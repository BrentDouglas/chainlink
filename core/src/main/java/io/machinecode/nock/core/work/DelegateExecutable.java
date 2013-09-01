package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Transport;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DelegateExecutable extends ExecutableImpl {

    private final Executable executable;

    public DelegateExecutable(final Executable executable) {
        this.executable = executable;
    }

    @Override
    public void run(final Transport transport) throws Exception {
        this.executable.execute(transport);
    }
}
