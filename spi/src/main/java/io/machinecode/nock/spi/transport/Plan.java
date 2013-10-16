package io.machinecode.nock.spi.transport;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Plan extends Serializable {

    int getMaxThreads();

    Executable[] getExecutables();

    TargetThread getTargetThread();

    String getElement();

    Plan[] then();

    Plan[] always();

    Plan[] fail();

    Plan[] cancel();
}
