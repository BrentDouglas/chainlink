package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.spi.element.partition.Strategy;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class StrategyValidator {

    private StrategyValidator(){}

    public static void validate(final Strategy that, final VisitorNode context) {
        if (that instanceof Mapper) {
            MapperValidator.INSTANCE.visit((Mapper) that, context);
        } else if (that instanceof Plan) {
            PlanValidator.INSTANCE.visit((Plan) that, context);
        }
    }
}
