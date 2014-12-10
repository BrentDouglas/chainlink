package io.machinecode.chainlink.core.validation.task;

import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.element.task.Task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class TaskValidator {

    private TaskValidator(){}

    public static void validate(final Task that, final VisitorNode context) {
        if (that instanceof Batchlet) {
            BatchletValidator.INSTANCE.visit((Batchlet) that, context);
        } else if (that instanceof Chunk) {
            ChunkValidator.INSTANCE.visit((Chunk) that, context);
        }
    }
}
