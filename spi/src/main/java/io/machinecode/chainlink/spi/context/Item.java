package io.machinecode.chainlink.spi.context;

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
