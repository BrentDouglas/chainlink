package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.validation.PropertyReferenceValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionMapperValidator extends PropertyReferenceValidator<PartitionMapper> {

    public static final PartitionMapperValidator INSTANCE = new PartitionMapperValidator();

    protected PartitionMapperValidator() {
        super("mapper");
    }
}
