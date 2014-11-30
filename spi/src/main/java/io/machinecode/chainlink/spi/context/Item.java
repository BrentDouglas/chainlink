package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Item {

    Serializable getData();

    BatchStatus getBatchStatus();

    String getExitStatus();
}
