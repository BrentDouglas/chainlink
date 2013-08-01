package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.validation.ValidationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class MapperValidator {

    public static void validate(final Mapper that, final ValidationContext context) {
        if (that instanceof PartitionMapper) {
            PartitionMapperValidator.INSTANCE.validate((PartitionMapper) that, context);
        } else if (that instanceof PartitionPlan) {
            PartitionPlanValidator.INSTANCE.validate((PartitionPlan) that, context);
        }
    }
}
