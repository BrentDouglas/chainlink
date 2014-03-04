package io.machinecode.chainlink.jsl.validation.task;

import io.machinecode.chainlink.jsl.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.element.task.Task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
