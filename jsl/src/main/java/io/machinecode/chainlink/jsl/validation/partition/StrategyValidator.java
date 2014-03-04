package io.machinecode.chainlink.jsl.validation.partition;

import io.machinecode.chainlink.jsl.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.partition.Mapper;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.spi.element.partition.Strategy;

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
