package io.machinecode.nock.spi;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Checkpoint extends Serializable {

    Serializable getReaderCheckpoint();

    Serializable getWriterCheckpoint();
}
