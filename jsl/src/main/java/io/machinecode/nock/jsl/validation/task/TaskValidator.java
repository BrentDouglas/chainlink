package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.element.task.Task;
import io.machinecode.nock.jsl.validation.ValidationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class TaskValidator {

    private TaskValidator(){}

    public static void validate(final Task that, final ValidationContext context) {
        if (that instanceof Batchlet) {
            BatchletValidator.INSTANCE.validate((Batchlet) that, context);
        } else if (that instanceof Chunk) {
            ChunkValidator.INSTANCE.validate((Chunk) that, context);
        }
    }
}
