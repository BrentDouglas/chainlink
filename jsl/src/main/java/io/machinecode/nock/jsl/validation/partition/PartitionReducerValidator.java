package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.PartitionReducer;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionReducerValidator extends PropertyReferenceValidator<PartitionReducer> {

    public static final PartitionReducerValidator INSTANCE = new PartitionReducerValidator();

    protected PartitionReducerValidator() {
        super("reducer");
    }
}
