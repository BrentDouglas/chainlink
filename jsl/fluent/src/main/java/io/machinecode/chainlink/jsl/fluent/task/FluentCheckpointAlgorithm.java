package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FluentCheckpointAlgorithm extends FluentPropertyReference<FluentCheckpointAlgorithm> implements CheckpointAlgorithm {
    @Override
    public FluentCheckpointAlgorithm copy() {
        return copy(new FluentCheckpointAlgorithm());
    }
}
