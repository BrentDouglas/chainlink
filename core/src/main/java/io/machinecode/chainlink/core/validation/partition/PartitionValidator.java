package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.jsl.partition.Partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionValidator extends ValidatingVisitor<Partition> {

    public static final PartitionValidator INSTANCE = new PartitionValidator();

    protected PartitionValidator() {
        super(Partition.ELEMENT);
    }

    @Override
    public void doVisit(final Partition that, final VisitorNode context) {
        if (that.getReducer() != null) {
            ReducerValidator.INSTANCE.visit(that.getReducer(), context);
        }
        if (that.getStrategy() != null) {
            StrategyValidator.validate(that.getStrategy(), context);
        }
        if (that.getAnalyzer() != null) {
            AnalyserValidator.INSTANCE.visit(that.getAnalyzer(), context);
        }
        if (that.getCollector() != null) {
            CollectorValidator.INSTANCE.visit(that.getCollector(), context);
        }
    }
}
