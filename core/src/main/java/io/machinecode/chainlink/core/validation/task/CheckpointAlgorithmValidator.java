package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.core.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmValidator extends PropertyReferenceValidator<CheckpointAlgorithm> {

    public static final CheckpointAlgorithmValidator INSTANCE = new CheckpointAlgorithmValidator();

    protected CheckpointAlgorithmValidator() {
        super(CheckpointAlgorithm.ELEMENT);
    }
}