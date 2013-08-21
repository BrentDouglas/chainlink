package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.jsl.validation.ValidationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class StrategyValidator {

    private StrategyValidator(){}

    public static void validate(final Strategy that, final ValidationContext context) {
        if (that instanceof Mapper) {
            MapperValidator.INSTANCE.validate((Mapper) that, context);
        } else if (that instanceof Plan) {
            PlanValidator.INSTANCE.validate((Plan) that, context);
        }
    }
}
