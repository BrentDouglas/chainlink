package io.machinecode.chainlink.core.context;

import io.machinecode.chainlink.spi.context.Item;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemImpl implements Item, Serializable {
    private static final long serialVersionUID = 1L;

    private final Serializable data;
    private final BatchStatus batchStatus;
    private final String exitStatus;

    public ItemImpl(final Serializable data, final BatchStatus batchStatus, final String exitStatus) {
        this.data = data;
        this.batchStatus = batchStatus;
        this.exitStatus = exitStatus;
    }

    @Override
    public Serializable getData() {
        return data;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }
}
