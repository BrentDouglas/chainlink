package io.machinecode.chainlink.jsl.validation.partition;

import io.machinecode.chainlink.jsl.visitor.ValidatingVisitor;
import io.machinecode.chainlink.jsl.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.partition.Partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
