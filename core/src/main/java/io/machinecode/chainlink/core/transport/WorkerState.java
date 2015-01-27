package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.WorkerId;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface WorkerState {

    DepthComparator COMPARATOR = new DepthComparator();

    WorkerId getId();

    int getExecutions();

    int getCallbacks();

    class DepthComparator implements Comparator<WorkerState>, Serializable {
        private static final long serialVersionUID = 0L;

        @Override
        public int compare(final WorkerState a, final WorkerState b) {
            int ret = Integer.compare(a.getExecutions(), b.getExecutions());
            if (ret != 0) {
                return ret;
            }
            return Integer.compare(a.getCallbacks(), b.getCallbacks());
        }
    }
}
