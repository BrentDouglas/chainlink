package io.machinecode.nock.jsl.validation.chunk;

import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.chunk.Chunk.CheckpointPolicy;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkValidator extends Validator<Chunk> {

    public static final ChunkValidator INSTANCE = new ChunkValidator();

    protected ChunkValidator() {
        super("chunk");
    }

    @Override
    public void doValidate(final Chunk that, final ValidationContext context) {
        if (that.getCheckpointPolicy() == null) {
            context.addProblem("Attribute 'checkpoint-policy' is required.");
        }
        if (!CheckpointPolicy.ITEM.equals(that.getCheckpointPolicy())
                && !CheckpointPolicy.CUSTOM.equals(that.getCheckpointPolicy())) {
            context.addProblem("Attribute 'checkpoint-policy' must match '" + CheckpointPolicy.ITEM + "' or '" + CheckpointPolicy.CUSTOM + "'. Found '" + that.getCheckpointPolicy() + "'.");
        }

        if (that.getItemCount() < 0) {
            context.addProblem("Attribute 'item-count' must be positive. Found '" + that.getItemCount() + "'.");
        }
        if (that.getTimeLimit() < 0) {
            context.addProblem("Attribute 'time-limit' must be positive. Found '" + that.getTimeLimit() + "'.");
        }
        if (that.getSkipLimit() < 0) {
            context.addProblem("Attribute 'skip-limit' must be positive. Found '" + that.getSkipLimit() + "'.");
        }
        if (that.getRetryLimit() < 0) {
            context.addProblem("Attribute 'retry-limit' must be positive. Found '" + that.getRetryLimit() + "'.");
        }

        if (that.getReader() == null) {
            context.addProblem("Element 'reader' must not be null.");
        }
        if (that.getProcessor() == null) {
            context.addProblem("Element 'processor' must not be null.");
        }
        if (that.getWriter() == null) {
            context.addProblem("Element 'writer' must not be null.");
        }

        //Nullable

        if (that.getCheckpointAlgorithm() != null) {
            CheckpointAlgorithmValidator.INSTANCE.validate(that.getCheckpointAlgorithm(), context);
        }
        if (that.getNoRollbackExceptionClasses() != null) {
            ExecutionClassFilterValidator.NO_ROLLBACK.validate(that.getNoRollbackExceptionClasses(), context);
        }
        if (that.getSkippableExceptionClasses() != null) {
            ExecutionClassFilterValidator.SKIPPABLE.validate(that.getSkippableExceptionClasses(), context);
        }
        if (that.getRetryableExceptionClasses() != null) {
            ExecutionClassFilterValidator.RETRYABLE.validate(that.getRetryableExceptionClasses(), context);
        }
    }
}
