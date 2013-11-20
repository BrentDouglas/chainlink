package io.machinecode.nock.spi.execution;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Item {

    Serializable getData();

    BatchStatus getBatchStatus();

    String getExitStatus();
}
