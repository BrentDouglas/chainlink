package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.task.CheckpointAlgorithm;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentCheckpointAlgorithm extends FluentPropertyReference<FluentCheckpointAlgorithm> implements CheckpointAlgorithm {
    @Override
    public FluentCheckpointAlgorithm copy() {
        return copy(new FluentCheckpointAlgorithm());
    }
}
