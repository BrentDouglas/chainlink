package io.machinecode.nock.core.model.task;

import io.machinecode.nock.spi.Checkpoint;

import java.io.Serializable;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public final class CheckpointImpl implements Checkpoint {

    private final Serializable reader;
    private final Serializable writer;

    public CheckpointImpl(final Serializable reader, final Serializable writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public Serializable getReaderCheckpoint() {
        return reader;
    }

    @Override
    public Serializable getWriterCheckpoint() {
        return writer;
    }
}
