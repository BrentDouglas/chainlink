package io.machinecode.nock.spi;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Checkpoint {

    Serializable getReaderCheckpoint();

    Serializable getWriterCheckpoint();
}
