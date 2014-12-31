package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.registry.WorkerId;

import java.io.Serializable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class WorkerIdAndAddress<A> implements Serializable {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;
    final A address;

    public WorkerIdAndAddress(final WorkerId workerId, final A address) {
        if (workerId == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        if (address == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        this.workerId = workerId;
        this.address = address;
    }

    public WorkerId getWorkerId() {
        return workerId;
    }

    public A getAddress() {
        return address;
    }
}
