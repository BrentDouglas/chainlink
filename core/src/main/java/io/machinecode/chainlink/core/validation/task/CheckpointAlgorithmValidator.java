package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;
import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CheckpointAlgorithmValidator extends PropertyReferenceValidator<CheckpointAlgorithm> {

    public static final CheckpointAlgorithmValidator INSTANCE = new CheckpointAlgorithmValidator();

    protected CheckpointAlgorithmValidator() {
        super(CheckpointAlgorithm.ELEMENT);
    }
}
