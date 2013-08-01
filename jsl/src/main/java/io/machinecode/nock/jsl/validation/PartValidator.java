package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.validation.chunk.ChunkValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class PartValidator {

    public static void validate(final Part that, final ValidationContext context) {
        if (that instanceof Batchlet) {
            BatchletValidator.INSTANCE.validate((Batchlet) that, context);
        } else if (that instanceof Chunk) {
            ChunkValidator.INSTANCE.validate((Chunk) that, context);
        }
    }
}
