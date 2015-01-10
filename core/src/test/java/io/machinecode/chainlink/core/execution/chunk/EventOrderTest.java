package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.base.OperatorTest;
import io.machinecode.chainlink.core.execution.chunk.artifact.EventOrderTransactionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class EventOrderTest extends OperatorTest {

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        super.visitJobOperatorModel(model);
        model.getTransactionManager().setValue(new EventOrderTransactionManager(180, TimeUnit.SECONDS));
    }
}
