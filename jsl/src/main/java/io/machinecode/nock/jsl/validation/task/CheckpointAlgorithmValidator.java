package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.jsl.api.task.CheckpointAlgorithm;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmValidator extends PropertyReferenceValidator<CheckpointAlgorithm> {

    public static final CheckpointAlgorithmValidator INSTANCE = new CheckpointAlgorithmValidator();

    protected CheckpointAlgorithmValidator() {
        super("checkpoint-algorithm");
    }
}
