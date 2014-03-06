package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentCheckpointAlgorithm extends FluentPropertyReference<FluentCheckpointAlgorithm> implements CheckpointAlgorithm {
    @Override
    public FluentCheckpointAlgorithm copy() {
        return copy(new FluentCheckpointAlgorithm());
    }
}
